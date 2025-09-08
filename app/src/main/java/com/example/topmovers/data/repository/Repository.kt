package com.example.topmovers.data.repository

import com.example.topmovers.data.model.CompanyInfo
import com.example.topmovers.data.remote.RetrofitInstance
import com.example.topmovers.data.model.SearchResult
import com.example.topmovers.data.model.StockDataPoint
import com.example.topmovers.data.model.TopMover
import com.example.topmovers.data.model.TopMoversResponse
import com.example.topmovers.data.model.WatchList
import com.example.topmovers.data.local.WatchlistDao
import com.example.topmovers.data.local.WatchlistStockCrossRef
import com.example.topmovers.data.local.WatchlistWithStocks
import kotlinx.coroutines.flow.Flow
import java.io.IOException
import java.util.concurrent.TimeUnit
import com.squareup.moshi.JsonDataException

class ApiLimitException(message: String) : IOException(message)

class Repository(private val watchlistDao: WatchlistDao) {

    companion object {
        // Cache duration for the list of top movers (30 minutes)
        private val TOP_MOVERS_CACHE_DURATION = TimeUnit.MINUTES.toMillis(30)
        // Cache duration for individual stock company details (24 hours)
        private val COMPANY_INFO_CACHE_DURATION = TimeUnit.HOURS.toMillis(24)
    }

    // --- DATABASE OPERATIONS ---

    val allWatchlists: Flow<List<WatchList>> = watchlistDao.getAllWatchlists()

    suspend fun addWatchlist(name: String): Long {
        val newWatchlist = WatchList(name = name)
        return watchlistDao.insertWatchlist(newWatchlist)
    }

    suspend fun removeWatchlist(watchlist: WatchList) {
        watchlistDao.deleteWatchlist(watchlist)
    }

    suspend fun addStockToWatchlist(stock: TopMover, watchlistId: Long) {
        // We must clear the cache-specific fields before inserting into a personal watchlist.
        val cleanStock = stock.copy(cacheType = "", lastFetched = 0L)
        watchlistDao.insertStock(cleanStock)
        val crossRef = WatchlistStockCrossRef(watchlistId = watchlistId, ticker = stock.ticker)
        watchlistDao.insertWatchlistStockCrossRef(crossRef)
    }

    // --- NETWORK (API) & CACHE OPERATIONS ---

    /**
     * Fetches the list of top gainers and losers from the network OR the local cache.
     */
    suspend fun getTopMoversFromApi(apiKey: String): TopMoversResponse {
        val currentTime = System.currentTimeMillis()
        val lastFetchedTime = watchlistDao.getTopMoversLastFetched("top_gainers")

        // 1. CHECK CACHE: If data exists and is not expired, serve from the database.
        if (lastFetchedTime != null && (currentTime - lastFetchedTime < TOP_MOVERS_CACHE_DURATION)) {
            val cachedGainers = watchlistDao.getTopMovers("top_gainers")
            val cachedLosers = watchlistDao.getTopMovers("top_losers")
            val cachedActives = watchlistDao.getTopMovers("most_actively_traded")

            return TopMoversResponse(
                information = null,
                metadata = "Data from cache",
                lastUpdated = "",
                topGainers = cachedGainers,
                topLosers = cachedLosers,
                mostActivelyTraded = cachedActives
            )
        }

        // 2. FETCH FROM NETWORK: If cache is stale or empty, make the API call.
        try {
            val response = RetrofitInstance.api.getTopMovers(apiKey = apiKey)
            if (response.information != null) {
                throw ApiLimitException("API rate limit reached. Please try again later.")
            }

            // 3. UPDATE CACHE: On success, clear old data and save new data with a timestamp.
            val timestamp = System.currentTimeMillis()

            response.topGainers?.let {
                val gainers = it.map { mover -> mover.copy(cacheType = "top_gainers", lastFetched = timestamp) }
                watchlistDao.deleteTopMovers("top_gainers")
                watchlistDao.insertTopMovers(gainers)
            }
            response.topLosers?.let {
                val losers = it.map { mover -> mover.copy(cacheType = "top_losers", lastFetched = timestamp) }
                watchlistDao.deleteTopMovers("top_losers")
                watchlistDao.insertTopMovers(losers)
            }
            response.mostActivelyTraded?.let {
                val actives = it.map { mover -> mover.copy(cacheType = "most_actively_traded", lastFetched = timestamp) }
                watchlistDao.deleteTopMovers("most_actively_traded")
                watchlistDao.insertTopMovers(actives)
            }
            return response
        } catch (e: Exception) {
            // 4. FALLBACK: On network error, try to serve stale data from the cache.
            val cachedGainers = watchlistDao.getTopMovers("top_gainers")
            if (cachedGainers.isNotEmpty()) {
                val cachedLosers = watchlistDao.getTopMovers("top_losers")
                val cachedActives = watchlistDao.getTopMovers("most_actively_traded")
                return TopMoversResponse(null, "Stale data from cache", "", cachedGainers, cachedLosers, cachedActives)
            }
            // If there's no cached data at all, re-throw the original exception.
            throw e
        }
    }

    /**
     * Fetches the detailed company overview for a single stock from the network OR the local cache.
     */
    suspend fun getCompanyOverview(ticker: String, apiKey: String): CompanyInfo {
        val currentTime = System.currentTimeMillis()
        val cachedInfo = watchlistDao.getCompanyInfo(ticker)

        // 1. CHECK CACHE: If data exists and is not expired, serve from the database.
        if (cachedInfo != null && (currentTime - cachedInfo.lastFetched < COMPANY_INFO_CACHE_DURATION)) {
            return cachedInfo
        }

        // 2. FETCH FROM NETWORK: If cache is stale or empty, make the API call.
        try {
            val networkInfo = RetrofitInstance.api.getCompanyOverview(symbol = ticker, apiKey = apiKey)

            // 3. UPDATE CACHE: On success, add a timestamp and save to the database.
            networkInfo.lastFetched = System.currentTimeMillis()
            watchlistDao.insertCompanyInfo(networkInfo)
            return networkInfo

            // NEW: Specifically catch the error caused by an empty API response.
        } catch (e: JsonDataException) {
            // Create a placeholder CompanyInfo object to show "N/A" in the UI.
            // This object is NOT saved to the cache.
            return CompanyInfo(
                symbol = ticker,
                name = ticker, // Use ticker as name for display
                description = "No data available for this symbol.",
                assetType = "N/A",
                sector = "N/A",
                industry = "N/A",
                marketCap = "N/A",
                peRatio = "N/A",
                beta = "N/A",
                dividendYield = "N/A",
                profitMargin = "N/A",
                week52High = "N/A",
                week52Low = "N/A",
                exchange = "N/A",
                lastFetched = 0L
            )

        } catch (e: Exception) {
            // 4. FALLBACK: On other network errors, if we have any stale data, serve that.
            cachedInfo?.let { return it }
            // If there's no cached data at all, re-throw the original exception.
            throw e
        }
    }

    fun getWatchlistWithStocks(id: Long): Flow<WatchlistWithStocks> {
        return watchlistDao.getWatchlistWithStocks(id)
    }

    suspend fun getQuoteForTicker(ticker: String, apiKey: String): Result<TopMover> {
        return try {
            val response = RetrofitInstance.api.getQuote(symbol = ticker, apiKey = apiKey)
            val freshStock = TopMover(
                ticker = response.globalQuote.symbol,
                price = response.globalQuote.price,
                changeAmount = response.globalQuote.change,
                changePercentage = response.globalQuote.changePercent,
                volume = ""
            )
            Result.success(freshStock)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getTimeSeriesData(function: String, ticker: String, apiKey: String): List<StockDataPoint> {
        val response = RetrofitInstance.api.getTimeSeries(
            function = function,
            symbol = ticker,
            interval = if (function == "TIME_SERIES_INTRADAY") "5min" else null,
            apiKey = apiKey
        )

        val timeSeriesMap = when {
            response.intradayData != null -> response.intradayData
            response.dailyData != null -> response.dailyData
            response.weeklyData != null -> response.weeklyData
            response.monthlyData != null -> response.monthlyData
            else -> emptyMap()
        }
        return timeSeriesMap.values.toList()
    }

    suspend fun searchTicker(query: String, apiKey: String): List<SearchResult> {
        return RetrofitInstance.api.searchSymbol(keywords = query, apiKey = apiKey).bestMatches
    }
    fun isStockInWatchlist(ticker: String): Flow<Boolean> {
        return watchlistDao.isStockInWatchlist(ticker)
    }

}
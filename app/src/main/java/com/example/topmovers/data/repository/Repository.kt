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
        private val TOP_MOVERS_CACHE_DURATION = TimeUnit.MINUTES.toMillis(30)
        private val COMPANY_INFO_CACHE_DURATION = TimeUnit.HOURS.toMillis(24)
    }


    val allWatchlists: Flow<List<WatchList>> = watchlistDao.getAllWatchlists()

    suspend fun addWatchlist(name: String): Long {
        val newWatchlist = WatchList(name = name)
        return watchlistDao.insertWatchlist(newWatchlist)
    }

    suspend fun removeWatchlist(watchlist: WatchList) {
        watchlistDao.deleteWatchlist(watchlist)
    }

    suspend fun addStockToWatchlist(stock: TopMover, watchlistId: Long) {
        val cleanStock = stock.copy(cacheType = "", lastFetched = 0L)
        watchlistDao.insertStock(cleanStock)
        val crossRef = WatchlistStockCrossRef(watchlistId = watchlistId, ticker = stock.ticker)
        watchlistDao.insertWatchlistStockCrossRef(crossRef)
    }



    suspend fun getTopMoversFromApi(apiKey: String): TopMoversResponse {
        val currentTime = System.currentTimeMillis()
        val lastFetchedTime = watchlistDao.getTopMoversLastFetched("top_gainers")

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

        try {
            val response = RetrofitInstance.api.getTopMovers(apiKey = apiKey)
            if (response.information != null) {
                throw ApiLimitException("API rate limit reached. Please try again later.")
            }

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
            val cachedGainers = watchlistDao.getTopMovers("top_gainers")
            if (cachedGainers.isNotEmpty()) {
                val cachedLosers = watchlistDao.getTopMovers("top_losers")
                val cachedActives = watchlistDao.getTopMovers("most_actively_traded")
                return TopMoversResponse(null, "Stale data from cache", "", cachedGainers, cachedLosers, cachedActives)
            }
            throw e
        }
    }


    suspend fun getCompanyOverview(ticker: String, apiKey: String): CompanyInfo {
        val currentTime = System.currentTimeMillis()
        val cachedInfo = watchlistDao.getCompanyInfo(ticker)

        if (cachedInfo != null && (currentTime - cachedInfo.lastFetched < COMPANY_INFO_CACHE_DURATION)) {
            return cachedInfo
        }

        try {
            val networkInfo = RetrofitInstance.api.getCompanyOverview(symbol = ticker, apiKey = apiKey)

            networkInfo.lastFetched = System.currentTimeMillis()
            watchlistDao.insertCompanyInfo(networkInfo)
            return networkInfo

        } catch (e: JsonDataException) {
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
            cachedInfo?.let { return it }
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
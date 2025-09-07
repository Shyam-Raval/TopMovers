
package com.example.topmovers.Repository

import com.example.topmovers.Retrofit.CompanyInfo
import com.example.topmovers.Retrofit.RetrofitInstance
import com.example.topmovers.Retrofit.TopMover
import com.example.topmovers.Retrofit.TopMoversResponse
import com.example.topmovers.Room.WatchList
import com.example.topmovers.Room.WatchlistDao
import com.example.topmovers.Room.WatchlistStockCrossRef
import com.example.topmovers.Room.WatchlistWithStocks
import kotlinx.coroutines.flow.Flow
import java.io.IOException

class ApiLimitException(message: String) : IOException(message)

/**
 * The repository requires the watchlistDao to talk to the database,
 * so we pass it in the constructor.
 */
class Repository(val watchlistDao: WatchlistDao) {

    // --- DATABASE OPERATIONS ---

    /**
     * A live stream of all watchlists from the database.
     */
    val allWatchlists: Flow<List<WatchList>> = watchlistDao.getAllWatchlists()

    /**
     * Adds a new watchlist to the database.
     */
    suspend fun addWatchlist(name: String): Long {
        val newWatchlist = WatchList(name = name)
        return watchlistDao.insertWatchlist(newWatchlist)

    }

    /**
     * Removes a watchlist from the database.
     */
    suspend fun removeWatchlist(watchlist: WatchList) {
        watchlistDao.deleteWatchlist(watchlist)
    }

    /**
     * Adds a stock to a specific watchlist.
     */
    suspend fun addStockToWatchlist(stock: TopMover, watchlistId: Long) {
        watchlistDao.insertStock(stock)
        val crossRef = WatchlistStockCrossRef(watchlistId = watchlistId, ticker = stock.ticker)
        watchlistDao.insertWatchlistStockCrossRef(crossRef)
    }

    // --- NETWORK (API) OPERATIONS ---

    /**
     * Fetches the list of top gainers and losers from the network.
     */
    suspend fun getTopMoversFromApi(apiKey: String): TopMoversResponse {
        val response = RetrofitInstance.api.getTopMovers(apiKey = apiKey)
        if (response.information != null) {
            throw ApiLimitException("API rate limit reached. Please try again later.")
        }
        return response
    }

    /**
     * Fetches the detailed company overview for a single stock from the network.
     */
    suspend fun getCompanyOverview(ticker: String, apiKey: String): CompanyInfo {
        return RetrofitInstance.api.getCompanyOverview(symbol = ticker, apiKey = apiKey)
    }
    fun getWatchlistWithStocks(id: Long): Flow<WatchlistWithStocks> {
        return watchlistDao.getWatchlistWithStocks(id)
    }
    suspend fun getQuoteForTicker(ticker: String): Result<TopMover> {
        return try {
            val response = RetrofitInstance.api.getQuote(symbol = ticker)
            // Convert the API response into the TopMover format your UI uses
            val freshStock = TopMover(
                ticker = response.globalQuote.symbol,
                price = response.globalQuote.price,
                changeAmount = response.globalQuote.change,
                changePercentage = response.globalQuote.changePercent,
                volume = "" // The quote endpoint doesn't provide volume
            )
            Result.success(freshStock)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


}

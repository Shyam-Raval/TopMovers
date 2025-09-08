// File: com/example/topmovers/Room/WatchlistDao.kt

package com.example.topmovers.data.local

import androidx.room.*
import com.example.topmovers.data.model.CompanyInfo // NEW: Import CompanyInfo
import com.example.topmovers.data.model.TopMover
import com.example.topmovers.data.model.WatchList
import kotlinx.coroutines.flow.Flow

@Dao
interface WatchlistDao {

    // --- Insert Operations (Existing) ---
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertWatchlist(watchlist: WatchList): Long
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertStock(stock: TopMover)
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertWatchlistStockCrossRef(crossRef: WatchlistStockCrossRef)

    // --- Delete Operations (Existing) ---
    @Delete
    suspend fun deleteWatchlist(watchlist: WatchList)
    @Delete
    suspend fun deleteStockFromWatchlist(crossRef: WatchlistStockCrossRef)

    // --- Query (Read) Operations (Existing) ---
    @Query("SELECT * FROM watchlists ORDER BY name ASC")
    fun getAllWatchlists(): Flow<List<WatchList>>
    @Transaction
    @Query("SELECT * FROM watchlists WHERE watchlistId = :watchlistId")
    fun getWatchlistWithStocks(watchlistId: Long): Flow<WatchlistWithStocks>
    @Query("SELECT EXISTS(SELECT 1 FROM watchlist_stock_cross_ref WHERE ticker = :ticker LIMIT 1)")
    fun isStockInWatchlist(ticker: String): Flow<Boolean>

    // --- NEW: CACHE OPERATIONS FOR TOP MOVERS ---

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTopMovers(movers: List<TopMover>)

    @Query("SELECT * FROM stocks WHERE cacheType = :type")
    suspend fun getTopMovers(type: String): List<TopMover>

    @Query("DELETE FROM stocks WHERE cacheType = :type")
    suspend fun deleteTopMovers(type: String)

    @Query("SELECT lastFetched FROM stocks WHERE cacheType = :type LIMIT 1")
    suspend fun getTopMoversLastFetched(type: String): Long?


    // --- NEW: CACHE OPERATIONS FOR COMPANY INFO ---

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCompanyInfo(info: CompanyInfo)

    @Query("SELECT * FROM company_info WHERE symbol = :ticker")
    suspend fun getCompanyInfo(ticker: String): CompanyInfo?
}
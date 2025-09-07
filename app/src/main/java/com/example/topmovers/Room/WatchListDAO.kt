package com.example.topmovers.Room

import androidx.room.*
import com.example.topmovers.Retrofit.TopMover
import kotlinx.coroutines.flow.Flow

@Dao
interface WatchlistDao {

    // --- Insert Operations ---

    /**
     * Inserts a new watchlist.
     * If a watchlist with the same primary key already exists, it will be ignored.
     * Returns the id of the newly inserted row.
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertWatchlist(watchlist: WatchList): Long

    /**
     * Inserts a stock's details into the 'stocks' table.
     * If a stock with the same ticker already exists, this operation is ignored.
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertStock(stock: TopMover)

    /**
     * Creates the link between a watchlist and a stock.
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertWatchlistStockCrossRef(crossRef: WatchlistStockCrossRef)


    // --- Delete Operations ---

    /**
     * Deletes a watchlist. Room will find it based on its primary key.
     */
    @Delete
    suspend fun deleteWatchlist(watchlist: WatchList)

    /**
     * Removes the link between a stock and a watchlist, effectively
     * deleting a stock from a specific list.
     */
    @Delete
    suspend fun deleteStockFromWatchlist(crossRef: WatchlistStockCrossRef)


    // --- Query (Read) Operations ---

    /**
     * Retrieves all of your watchlists, ordered alphabetically.
     * It returns a Flow, so your UI can automatically update when the data changes.
     */
    @Query("SELECT * FROM watchlists ORDER BY name ASC")
    fun getAllWatchlists(): Flow<List<WatchList>>

    /**
     * Retrieves a single watchlist and all of the stocks it contains.
     * @Transaction ensures this happens atomically (all at once).
     */
    @Transaction
    @Query("SELECT * FROM watchlists WHERE watchlistId = :watchlistId")
    fun getWatchlistWithStocks(watchlistId: Long): Flow<WatchlistWithStocks>

    /**
     * Checks if a specific stock ticker exists in any watchlist.
     * Returns a Flow of true if it exists, false otherwise.
     */
    @Query("SELECT EXISTS(SELECT 1 FROM watchlist_stock_cross_ref WHERE ticker = :ticker LIMIT 1)")
    fun isStockInWatchlist(ticker: String): Flow<Boolean>
}
package com.example.topmovers.data.local

import androidx.room.Entity

@Entity(tableName = "watchlist_stock_cross_ref", primaryKeys = ["watchlistId", "ticker"])
data class WatchlistStockCrossRef(
    val watchlistId: Long,
    val ticker: String
)
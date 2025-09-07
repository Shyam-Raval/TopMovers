    package com.example.topmovers.Room


    import androidx.room.Embedded
    import androidx.room.Junction // <-- THE MISSING IMPORT IS NOW ADDED
    import androidx.room.Relation
    import com.example.topmovers.Retrofit.TopMover

    data class WatchlistWithStocks(
        @Embedded
        val watchlist: WatchList,

        @Relation(
            parentColumn = "watchlistId",
            entityColumn = "ticker",
            associateBy = Junction(WatchlistStockCrossRef::class)
        )
        val stocks: List<TopMover>
    )
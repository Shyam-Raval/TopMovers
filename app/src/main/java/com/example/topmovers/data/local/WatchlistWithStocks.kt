    package com.example.topmovers.data.local


    import androidx.room.Embedded
    import androidx.room.Junction // <-- THE MISSING IMPORT IS NOW ADDED
    import androidx.room.Relation
    import com.example.topmovers.data.model.TopMover
    import com.example.topmovers.data.model.WatchList

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
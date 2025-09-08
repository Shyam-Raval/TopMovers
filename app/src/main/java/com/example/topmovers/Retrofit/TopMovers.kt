// File: com/example/topmovers/Retrofit/TopMover.kt

package com.example.topmovers.Retrofit

import androidx.room.Entity
import com.squareup.moshi.Json

// MODIFICATION: Add cacheType and lastFetched, and define a composite primary key.
@Entity(tableName = "stocks", primaryKeys = ["ticker", "cacheType"])
data class TopMover(
    // The @PrimaryKey annotation is removed from here
    @Json(name = "ticker")
    val ticker: String,

    @Json(name = "price")
    val price: String,

    @Json(name = "change_amount")
    val changeAmount: String,

    @Json(name = "change_percentage")
    val changePercentage: String,

    @Json(name = "volume")
    val volume: String,

    // NEW: A field to distinguish between "top_gainers", "top_losers", etc.
    var cacheType: String = "",

    // NEW: A timestamp to check for cache expiration.
    var lastFetched: Long = 0L
)
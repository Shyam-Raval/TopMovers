
package com.example.topmovers.data.model

import androidx.room.Entity
import com.squareup.moshi.Json

@Entity(tableName = "stocks", primaryKeys = ["ticker", "cacheType"])
data class TopMover(
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

    var cacheType: String = "",

    var lastFetched: Long = 0L
)
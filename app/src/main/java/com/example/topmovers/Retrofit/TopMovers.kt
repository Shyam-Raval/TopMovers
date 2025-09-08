package com.example.topmovers.Retrofit

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json // Use Moshi's annotation

@Entity(tableName = "stocks")
data class TopMover(
    @PrimaryKey
    @Json(name = "ticker")
    val ticker: String,

    @Json(name = "price")
    val price: String,

    // THE FIX IS HERE: Corrected the property name from changeAMount to changeAmount
    @Json(name = "change_amount")
    val changeAmount: String,

    @Json(name = "change_percentage")
    val changePercentage: String,

    @Json(name = "volume")
    val volume: String
)
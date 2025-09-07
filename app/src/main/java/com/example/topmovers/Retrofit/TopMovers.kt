package com.example.topmovers.Retrofit

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "stocks")
data class TopMover(
    @PrimaryKey
    @SerializedName("ticker")
    val ticker: String,

    @SerializedName("price")
    val price: String,

    @SerializedName("change_amount")
    val changeAmount: String,

    @SerializedName("change_percentage")
    val changePercentage: String,

    @SerializedName("volume")
    val volume: String
)

package com.example.topmovers.data.model

import com.squareup.moshi.Json

data class TimeSeriesResponse(
    @Json(name = "Meta Data")
    val metaData: MetaData,

    @Json(name = "Time Series (5min)")
    val intradayData: Map<String, StockDataPoint>? = null,

    @Json(name = "Time Series (Daily)")
    val dailyData: Map<String, StockDataPoint>? = null,

    @Json(name = "Weekly Time Series")
    val weeklyData: Map<String, StockDataPoint>? = null,

    @Json(name = "Monthly Time Series")
    val monthlyData: Map<String, StockDataPoint>? = null
)

data class MetaData(
    @Json(name = "2. Symbol")
    val symbol: String
)

data class StockDataPoint(
    @Json(name = "1. open")
    val open: String,

    @Json(name = "2. high")
    val high: String,

    @Json(name = "3. low")
    val low: String,

    @Json(name = "4. close")
    val close: String,

    @Json(name = "5. volume")
    val volume: String
)
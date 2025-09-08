package com.example.topmovers.Retrofit

import com.squareup.moshi.Json

// This is the main container for the entire API response.
// We make each time series nullable because only one will be present in any given API call.
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

// This holds the metadata, like the stock symbol.
data class MetaData(
    @Json(name = "2. Symbol")
    val symbol: String
)

// This represents a single data point on the chart (e.g., one day's prices).
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
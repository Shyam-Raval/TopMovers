package com.example.topmovers.data.model

import com.squareup.moshi.Json

data class GlobalQuoteResponse(
    @Json(name = "Global Quote")
    val globalQuote: GlobalQuote
)

data class GlobalQuote(
    @Json(name = "01. symbol")
    val symbol: String,
    @Json(name = "05. price")
    val price: String,
    @Json(name = "09. change")
    val change: String,
    @Json(name = "10. change percent")
    val changePercent: String
)
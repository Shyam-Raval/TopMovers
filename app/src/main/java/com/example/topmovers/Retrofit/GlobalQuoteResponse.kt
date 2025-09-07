package com.example.topmovers.Retrofit

 // Or your appropriate package

import com.google.gson.annotations.SerializedName

// This class matches the outer "Global Quote" object in the JSON
data class GlobalQuoteResponse(
    @SerializedName("Global Quote")
    val globalQuote: GlobalQuote
)

// This class matches the fields inside the "Global Quote" object
data class GlobalQuote(
    @SerializedName("01. symbol")
    val symbol: String,

    @SerializedName("05. price")
    val price: String,

    @SerializedName("09. change")
    val change: String,

    @SerializedName("10. change percent")
    val changePercent: String
)
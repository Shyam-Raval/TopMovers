package com.example.topmovers.Retrofit

import com.google.gson.annotations.SerializedName


data class TopMoversResponse(
        // ADD THIS FIELD for the rate limit message
        @SerializedName("Information")
        val information: String?,

        @SerializedName("metadata")
        val metadata: String,

        @SerializedName("last_updated")
        val lastUpdated: String,

        @SerializedName("top_gainers")
        val topGainers: List<TopMover>,

        @SerializedName("top_losers")
        val topLosers: List<TopMover>,

        @SerializedName("most_actively_traded")
        val mostActivelyTraded: List<TopMover>
    )

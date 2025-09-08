package com.example.topmovers.data.model

import com.squareup.moshi.Json // Use Moshi's annotation

data class TopMoversResponse(
    @Json(name = "Information")
        val information: String?,

    @Json(name = "metadata")
        val metadata: String?,

        // THE FIX IS HERE: "last_updated" now correctly maps to the "lastUpdated" property
    @Json(name = "last_updated")
        val lastUpdated: String?,

    @Json(name = "top_gainers")
        val topGainers: List<TopMover>?,

    @Json(name = "top_losers")
        val topLosers: List<TopMover>?,

    @Json(name = "most_actively_traded")
        val mostActivelyTraded: List<TopMover>?
)
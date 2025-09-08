package com.example.topmovers.data.model


import com.squareup.moshi.Json

data class SymbolSearchResponse(
    @Json(name = "bestMatches")
    val bestMatches: List<SearchResult>
)

data class SearchResult(
    @Json(name = "1. symbol")
    val symbol: String,

    @Json(name = "2. name")
    val name: String,

    @Json(name = "4. region")
    val region: String
)
// File: com/example/topmovers/Retrofit/CompanyInfo.kt

package com.example.topmovers.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json

// MODIFICATION: Add @Entity annotation, a @PrimaryKey, and a lastFetched field.
@Entity(tableName = "company_info")
data class CompanyInfo(
    @PrimaryKey // The stock symbol is the unique key for this table.
    @Json(name = "Symbol")
    val symbol: String, // Made non-nullable to serve as a valid primary key.

    @Json(name = "Name")
    val name: String?,
    @Json(name = "Description")
    val description: String?,
    @Json(name = "AssetType")
    val assetType: String?,
    @Json(name = "Sector")
    val sector: String?,
    @Json(name = "Industry")
    val industry: String?,
    @Json(name = "MarketCapitalization")
    val marketCap: String?,
    @Json(name = "PERatio")
    val peRatio: String?,
    @Json(name = "Beta")
    val beta: String?,
    @Json(name = "DividendYield")
    val dividendYield: String?,
    @Json(name = "ProfitMargin")
    val profitMargin: String?,
    @Json(name = "52WeekHigh")
    val week52High: String?,
    @Json(name = "52WeekLow")
    val week52Low: String?,
    @Json(name = "Exchange")
    val exchange: String?,

    // NEW: A timestamp to check for cache expiration.
    var lastFetched: Long = 0L
)
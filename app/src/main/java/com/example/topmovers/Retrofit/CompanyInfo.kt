
package com.example.topmovers.Retrofit

import com.google.gson.annotations.SerializedName

data class CompanyInfo(
    @SerializedName("Symbol")
    val symbol: String?,
    @SerializedName("Name")
    val name: String?,
    @SerializedName("Description")
    val description: String?,
    // ADD THIS LINE
    @SerializedName("AssetType")
    val assetType: String?,
    @SerializedName("Sector")
    val sector: String?,
    @SerializedName("Industry")
    val industry: String?,
    @SerializedName("MarketCapitalization")
    val marketCap: String?,
    @SerializedName("PERatio")
    val peRatio: String?,
    @SerializedName("Beta")
    val beta: String?,
    @SerializedName("DividendYield")
    val dividendYield: String?,
    @SerializedName("ProfitMargin")
    val profitMargin: String?,
    @SerializedName("52WeekHigh")
    val week52High: String?,
    @SerializedName("52WeekLow")
    val week52Low: String?,
    @SerializedName("Exchange")
    val exchange: String?,

    )

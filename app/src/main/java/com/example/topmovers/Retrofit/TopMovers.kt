    package com.example.topmovers.Retrofit

    import com.google.gson.annotations.SerializedName

    data class TopMover(
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

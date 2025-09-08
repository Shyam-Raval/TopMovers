package com.example.topmovers.navigation

import java.net.URLEncoder
import java.nio.charset.StandardCharsets

sealed class Screens(val route: String) {
    object Explore : Screens("explore")

    object TopGainers : Screens("top_gainers")

    object TopLosers : Screens("top_losers")

    object Watchlist : Screens("watchlist")



    object StockDetails :
        Screens("stock_details/{ticker}?price={price}&changeAmount={changeAmount}&changePercentage={changePercentage}") {

        fun createRoute(ticker: String, price: String, changeAmount: String, changePercentage: String): String {
            val encodedChangeAmount =
                URLEncoder.encode(changeAmount, StandardCharsets.UTF_8.toString())
            val encodedChangePercentage =
                URLEncoder.encode(changePercentage, StandardCharsets.UTF_8.toString())
            return "stock_details/$ticker?price=$price&changeAmount=$encodedChangeAmount&changePercentage=$encodedChangePercentage"
        }
    }

    object WatchlistDetail : Screens("watchlist_detail/{watchlistId}") {
        fun createRoute(watchlistId: Long) = "watchlist_detail/$watchlistId"
    }
}

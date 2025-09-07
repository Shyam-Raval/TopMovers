package com.example.topmovers.navigation

import java.net.URLEncoder
import java.nio.charset.StandardCharsets

sealed class Screens(val route: String) {
    // Main screen with Top Gainers and Losers sections
    object Explore : Screens("explore")

    // "View All" screen for the complete list of top gainers
    object TopGainers : Screens("top_gainers")

    // "View All" screen for the complete list of top losers
    object TopLosers : Screens("top_losers")

    // Screen to display the user's watchlists
    object Watchlist : Screens("watchlist")

    // Details screen for a specific stock, requires a ticker symbol
    // The route now includes a placeholder for the price


    object StockDetails :
    // 1. ADD "changeAmount" to the route string
        Screens("stock_details/{ticker}?price={price}&changeAmount={changeAmount}&changePercentage={changePercentage}") {

        // 2. UPDATE the createRoute function to accept and pass the changeAmount
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

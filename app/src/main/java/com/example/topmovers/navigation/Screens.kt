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
        Screens("stock_details/{ticker}?price={price}&changePercentage={changePercentage}") {
        fun createRoute(ticker: String, price: String, changePercentage: String): String {
            val encodedChangePercentage =
                URLEncoder.encode(changePercentage, StandardCharsets.UTF_8.toString())
            return "stock_details/$ticker?price=$price&changePercentage=$encodedChangePercentage"
        }

    }
}

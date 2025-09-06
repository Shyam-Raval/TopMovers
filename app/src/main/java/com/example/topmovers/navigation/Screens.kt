package com.example.topmovers.navigation

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
    object StockDetails : Screens("stock_details/{ticker}") {
        fun createRoute(ticker: String) = "stock_details/$ticker"
    }
}

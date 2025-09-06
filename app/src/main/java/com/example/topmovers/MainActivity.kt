package com.example.topmovers

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.topmovers.Repository.Repository
import com.example.topmovers.Screens.DetailsScreen
import com.example.topmovers.Screens.ExploreScreen
import com.example.topmovers.Screens.TopGainersScreen
import com.example.topmovers.Screens.TopLosersScreen
import com.example.topmovers.Screens.WatchlistScreen // 1. Import WatchlistScreen
import com.example.topmovers.ViewModel.TopMoversViewModel
import com.example.topmovers.ViewModel.TopMoversViewModelFactory
import com.example.topmovers.ViewModel.WatchlistViewModel // Import your Watchlist ViewModel & Factory
import com.example.topmovers.ViewModel.WatchlistViewModelFactory
import com.example.topmovers.navigation.Screens
import com.example.topmovers.ui.theme.TopMoversTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val repository = Repository(applicationContext)

        // ViewModel for TopMovers (Gainers/Losers)
        val viewModel: TopMoversViewModel by viewModels {
            TopMoversViewModelFactory(repository)
        }

        // 2. ViewModel for Watchlist
        val watchlistViewModel: WatchlistViewModel by viewModels {
            WatchlistViewModelFactory(repository) // Assuming you create this factory
        }

        enableEdgeToEdge()
        setContent {
            TopMoversTheme {
                // The Scaffold should wrap the NavHost to provide a consistent structure
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val navController = rememberNavController()

                    NavHost(
                        navController = navController,
                        startDestination = Screens.Explore.route
                    ) {
                        // Explore Screen Route
                        composable(Screens.Explore.route) {
                            ExploreScreen(
                                viewModel = viewModel,
                                navController = navController,
                                onNavigateToTopGainers = {
                                    navController.navigate(Screens.TopGainers.route)
                                },
                                onNavigateToTopLosers = {
                                    navController.navigate(Screens.TopLosers.route)
                                }
                            )
                        }

                        // 3. ADD THE WATCHLIST SCREEN ROUTE
                        composable(Screens.Watchlist.route) {
                            WatchlistScreen(
                                viewModel = watchlistViewModel,
                                navController = navController
                            )
                        }

                        // Top Gainers Screen Route
                        composable(Screens.TopGainers.route) {
                            TopGainersScreen(
                                topGainers = viewModel.topGainers,
                                onBackClicked = {
                                    navController.popBackStack()
                                },
                                navController = navController

                            )
                        }

                        // Top Losers Screen Route
                        composable(Screens.TopLosers.route) {
                            TopLosersScreen(
                                topLosers = viewModel.topLosers,
                                onBackClicked = {
                                    navController.popBackStack()
                                },
                                navController = navController

                            )
                        }
                        // *** ADDED: Stock Details Screen Route ***
                        // In the file where your NavHost is defined

                        composable(
                            route = Screens.StockDetails.route,
                            arguments = listOf(
                                navArgument("price") { type = NavType.StringType; nullable = true },
                                navArgument("changePercentage") { type = NavType.StringType; nullable = true }
                            )
                        ) { backStackEntry ->
                            val ticker = backStackEntry.arguments?.getString("ticker")
                            val price = backStackEntry.arguments?.getString("price") ?: "N/A"
                            val changePercentage = backStackEntry.arguments?.getString("changePercentage") ?: "0.0%"

                            if (ticker != null) {
                                DetailsScreen(
                                    ticker = ticker,
                                    price = price,
                                    changePercentage = changePercentage, // Pass only the percentage
                                    apiKey = "NTJBDU9U1JGKA613",
                                    repository = repository,
                                    onBackClicked = { navController.popBackStack() }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
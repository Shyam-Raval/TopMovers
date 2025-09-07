package com.example.topmovers

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
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
import com.example.topmovers.Screens.WatchlistDetailScreen
import com.example.topmovers.Screens.WatchlistScreen // 1. Import WatchlistScreen
import com.example.topmovers.ViewModel.TopMoversViewModel
import com.example.topmovers.ViewModel.TopMoversViewModelFactory
import com.example.topmovers.ViewModel.WatchlistDetailViewModel
import com.example.topmovers.ViewModel.WatchlistDetailViewModelFactory
import com.example.topmovers.ViewModel.WatchlistViewModel // Import your Watchlist ViewModel & Factory
import com.example.topmovers.ViewModel.WatchlistViewModelFactory
import com.example.topmovers.navigation.Screens
import com.example.topmovers.ui.theme.TopMoversTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val application = applicationContext as MyStocksApp
        val repository = application.repository


        // ViewModel for TopMovers (Gainers/Losers)
        val viewModel: TopMoversViewModel by viewModels {
            TopMoversViewModelFactory(repository)
        }

        val watchlistViewModel: WatchlistViewModel by viewModels {
            WatchlistViewModelFactory(repository)
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
                                navArgument("ticker") { type = NavType.StringType },
                                navArgument("price") { type = NavType.StringType },
                                // 1. ADD the navArgument for changeAmount
                                navArgument("changeAmount") {
                                    type = NavType.StringType
                                    defaultValue = "0.00" // Provide a safe default
                                },
                                navArgument("changePercentage") { type = NavType.StringType }
                            )
                        ) { backStackEntry ->
                            val ticker = backStackEntry.arguments?.getString("ticker") ?: ""
                            val price = backStackEntry.arguments?.getString("price") ?: "N/A"
                            // 2. RETRIEVE the changeAmount from the backStackEntry
                            val changeAmount = backStackEntry.arguments?.getString("changeAmount") ?: "0.00"
                            val changePercentage = backStackEntry.arguments?.getString("changePercentage") ?: "0.0%"

                            DetailsScreen(
                                ticker = ticker,
                                price = price,
                                changePercentage = changePercentage,
                                changeAmount = changeAmount, // 3. PASS it to the DetailsScreen
                                repository = repository,
                                onBackClicked = { navController.popBackStack() }
                            )
                        }



                        composable(Screens.Watchlist.route) {
                            WatchlistScreen(
                                viewModel = watchlistViewModel,
                                navController = navController
                            )
                        }
                        composable(
                            route = Screens.WatchlistDetail.route,
                            arguments = listOf(navArgument("watchlistId") { type = NavType.LongType })
                        ) { backStackEntry ->
                            val watchlistId = backStackEntry.arguments?.getLong("watchlistId") ?: -1L

                            // Create the ViewModel using the factory and the ID from the route
                            val watchlistDetailViewModel: WatchlistDetailViewModel = viewModel(
                                factory = WatchlistDetailViewModelFactory(repository, watchlistId)
                            )

                            WatchlistDetailScreen(
                                viewModel = watchlistDetailViewModel,
                                navController = navController,
                                onBackClicked = { navController.popBackStack() }
                            )
                        }


                    }
                }
            }
        }
    }
}
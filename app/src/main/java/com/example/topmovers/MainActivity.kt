package com.example.topmovers

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.topmovers.Screens.DetailsScreen
import com.example.topmovers.Screens.ExploreScreen
import com.example.topmovers.Screens.TopGainersScreen
import com.example.topmovers.Screens.TopLosersScreen
import com.example.topmovers.Screens.WatchlistDetailScreen
import com.example.topmovers.Screens.WatchlistScreen
import com.example.topmovers.ViewModel.TopMoversViewModel
import com.example.topmovers.ViewModel.WatchlistDetailViewModel
import com.example.topmovers.ViewModel.WatchlistViewModel
import com.example.topmovers.navigation.Screens
import com.example.topmovers.ui.theme.TopMoversTheme
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        enableEdgeToEdge()
        setContent {
            // ViewModel instances are now retrieved from Koin
            val viewModel: TopMoversViewModel = koinViewModel()
            val watchlistViewModel: WatchlistViewModel = koinViewModel()
            TopMoversTheme {
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

                        // Watchlist Screen Route
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
                                onBackClicked = { navController.popBackStack() },
                                navController = navController
                            )
                        }

                        // Top Losers Screen Route
                        composable(Screens.TopLosers.route) {
                            TopLosersScreen(
                                topLosers = viewModel.topLosers,
                                onBackClicked = { navController.popBackStack() },
                                navController = navController
                            )
                        }

                        // Stock Details Screen Route
                        composable(
                            route = Screens.StockDetails.route,
                            arguments = listOf(
                                navArgument("ticker") { type = NavType.StringType },
                                navArgument("price") { type = NavType.StringType },
                                navArgument("changeAmount") {
                                    type = NavType.StringType
                                    defaultValue = "0.00"
                                },
                                navArgument("changePercentage") { type = NavType.StringType }
                            )
                        ) { backStackEntry ->
                            val ticker = backStackEntry.arguments?.getString("ticker") ?: ""
                            val price = backStackEntry.arguments?.getString("price") ?: "N/A"
                            val changeAmount = backStackEntry.arguments?.getString("changeAmount") ?: "0.00"
                            val changePercentage = backStackEntry.arguments?.getString("changePercentage") ?: "0.0%"

                            DetailsScreen(
                                ticker = ticker,
                                price = price,
                                changePercentage = changePercentage,
                                changeAmount = changeAmount,
                                onBackClicked = { navController.popBackStack() }
                            )
                        }

                        // Watchlist Detail Screen Route
                        composable(
                            route = Screens.WatchlistDetail.route,
                            arguments = listOf(navArgument("watchlistId") { type = NavType.LongType })
                        ) { backStackEntry ->
                            val watchlistId = backStackEntry.arguments?.getLong("watchlistId") ?: -1L

                            // Create the ViewModel using Koin, passing the watchlistId as a parameter
                            val watchlistDetailViewModel: WatchlistDetailViewModel = koinViewModel(
                                parameters = { parametersOf(watchlistId) }
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
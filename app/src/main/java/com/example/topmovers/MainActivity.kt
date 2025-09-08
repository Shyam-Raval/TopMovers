package com.example.topmovers

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
        setContent {
            // State to hold the current theme choice (light/dark)
            var useDarkTheme by remember { mutableStateOf(false) }

            // Pass the state to our theme, which will wrap the entire app
            TopMoversTheme(darkTheme = useDarkTheme) {
                val viewModel: TopMoversViewModel = koinViewModel()
                val watchlistViewModel: WatchlistViewModel = koinViewModel()
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = Screens.Explore.route
                ) {
                    composable(Screens.Explore.route) {
                        ExploreScreen(
                            viewModel = viewModel,
                            navController = navController,
                            // Pass the current theme state and the toggle function
                            useDarkTheme = useDarkTheme,
                            onThemeToggle = { useDarkTheme = !useDarkTheme },
                            onNavigateToTopGainers = {
                                navController.navigate(Screens.TopGainers.route)
                            },
                            onNavigateToTopLosers = {
                                navController.navigate(Screens.TopLosers.route)
                            }
                        )
                    }

                    composable(Screens.Watchlist.route) {
                        WatchlistScreen(
                            viewModel = watchlistViewModel,
                            navController = navController
                        )
                    }

                    composable(Screens.TopGainers.route) {
                        TopGainersScreen(
                            topGainers = viewModel.topGainers,
                            onBackClicked = { navController.popBackStack() },
                            navController = navController
                        )
                    }

                    composable(Screens.TopLosers.route) {
                        TopLosersScreen(
                            topLosers = viewModel.topLosers,
                            onBackClicked = { navController.popBackStack() },
                            navController = navController
                        )
                    }

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
                        val changeAmount =
                            backStackEntry.arguments?.getString("changeAmount") ?: "0.00"
                        val changePercentage =
                            backStackEntry.arguments?.getString("changePercentage") ?: "0.0%"

                        DetailsScreen(
                            ticker = ticker,
                            price = price,
                            changePercentage = changePercentage,
                            changeAmount = changeAmount,
                            onBackClicked = { navController.popBackStack() }
                        )
                    }

                    composable(
                        route = Screens.WatchlistDetail.route,
                        arguments = listOf(navArgument("watchlistId") {
                            type = NavType.LongType
                        })
                    ) { backStackEntry ->
                        val watchlistId =
                            backStackEntry.arguments?.getLong("watchlistId") ?: -1L
                        val watchlistDetailViewModel: WatchlistDetailViewModel =
                            koinViewModel(
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
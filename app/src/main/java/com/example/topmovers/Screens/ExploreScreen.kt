package com.example.topmovers.Screens


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.topmovers.ViewModel.TopMoversViewModel
import com.example.topmovers.navigation.Screens
import com.example.topmovers.ui.components.TopMoverItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreen(
    viewModel: TopMoversViewModel,
    navController: NavHostController,
    onNavigateToTopGainers: () -> Unit,
    onNavigateToTopLosers: () -> Unit
) {
    // Observe the state from the ViewModel
    val isLoading = viewModel.isLoading
    val errorMessage = viewModel.errorMessage
    val topGainers = viewModel.topGainers
    val topLosers = viewModel.topLosers

    Scaffold(
        topBar = {
            StocksTopAppBar(
                onSearch = { query ->
                    // TODO: Implement search logic in your ViewModel
                }
            )
        },
        bottomBar = { StocksBottomNav(navController = navController) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                // --- Loading State ---
                isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                // --- Error State ---
                errorMessage != null -> {
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp),
                        textAlign = TextAlign.Center
                    )
                }
                // --- Success State ---
                else -> {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // --- Top Gainers Section ---
                        if (topGainers.isNotEmpty()) {
                            item(span = { GridItemSpan(maxLineSpan) }) {
                                SectionHeader(
                                    title = "Top Gainers",
                                    onViewAllClicked = onNavigateToTopGainers
                                )
                            }
                            items(topGainers.take(4)) { mover ->
                                // CHANGED: Implemented onClick navigation
                                TopMoverItem(mover = mover) { ticker ->
                                    navController.navigate(
                                        Screens.StockDetails.createRoute(
                                            ticker = mover.ticker,
                                            price = mover.price,
                                            changePercentage = mover.changePercentage,
                                            changeAmount = mover.changeAmount
                                        )
                                    )


                                }
                            }
                        }

                        // --- Top Losers Section ---
                        if (topLosers.isNotEmpty()) {
                            item(span = { GridItemSpan(maxLineSpan) }) {
                                SectionHeader(
                                    title = "Top Losers",
                                    onViewAllClicked = onNavigateToTopLosers
                                )
                            }
                            items(topLosers.take(4)) { mover ->
                                // CHANGED: Implemented onClick navigation
                                TopMoverItem(mover = mover) { ticker ->
                                    navController.navigate(
                                        Screens.StockDetails.createRoute(
                                            ticker = mover.ticker,
                                            price = mover.price,
                                            changePercentage = mover.changePercentage,
                                            changeAmount = mover.changeAmount
                                        )
                                    )


                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StocksTopAppBar(onSearch: (String) -> Unit) { // CHANGED: Added onSearch lambda
    var searchText by remember { mutableStateOf("") }

    TopAppBar(
        title = { Text("TopMovers", fontWeight = FontWeight.Bold, maxLines = 1) },
        actions = {
            TextField(
                value = searchText,
                onValueChange = {
                    searchText = it
                    onSearch(it) // CHANGED: Call the search handler
                },
                placeholder = { Text("Search here...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search Icon") },
                modifier = Modifier.padding(end = 16.dp),
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent
                )
            )
        }
    )
}

@Composable
fun StocksBottomNav(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavigationBar {
        // --- Home Item ---
        NavigationBarItem(
            label = { Text("Home") },
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            selected = currentDestination?.hierarchy?.any { it.route == Screens.Explore.route } == true,
            onClick = {
                navController.navigate(Screens.Explore.route) {
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        )

        // --- Watchlist Item ---
        NavigationBarItem(
            label = { Text("Watchlist") },
            icon = { Icon(Icons.Default.Star, contentDescription = "Watchlist") },
            selected = currentDestination?.hierarchy?.any { it.route == Screens.Watchlist.route } == true,
            onClick = {
                navController.navigate(Screens.Watchlist.route) {
                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        )
    }
}

@Composable
fun SectionHeader(
    title: String,
    onViewAllClicked: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 8.dp, end = 8.dp, bottom = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        TextButton(onClick = onViewAllClicked) {
            Text(text = "View All")
        }
    }
}
package com.example.topmovers.Screens


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.topmovers.Retrofit.SearchResult
import com.example.topmovers.Retrofit.TopMover
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
    // Observe the existing states from the ViewModel
    val isLoading = viewModel.isLoading
    val errorMessage = viewModel.errorMessage
    val topGainers = viewModel.topGainers
    val topLosers = viewModel.topLosers

    // Observe the new search states from the ViewModel
    val searchQuery = viewModel.searchQuery
    val searchResults = viewModel.searchResults
    val isSearching = viewModel.isSearching

    Scaffold(
        topBar = {
            StocksTopAppBar(
                searchText = searchQuery,
                onSearchChanged = { viewModel.onSearchQueryChanged(it) },
                onClearSearch = { viewModel.clearSearch() }
            )
        },
        bottomBar = { StocksBottomNav(navController = navController) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Your original content for Top Movers
            TopMoversContent(
                isLoading = isLoading,
                errorMessage = errorMessage,
                topGainers = topGainers,
                topLosers = topLosers,
                navController = navController,
                onNavigateToTopGainers = onNavigateToTopGainers,
                onNavigateToTopLosers = onNavigateToTopLosers
            )

            // Conditionally display the search overlay
            if (searchQuery.isNotBlank()) {
                SearchOverlay(
                    isSearching = isSearching,
                    results = searchResults,
                    onResultClick = { ticker ->
                        // When a result is clicked, navigate to the details screen
                        navController.navigate(
                            Screens.StockDetails.createRoute(
                                ticker = ticker,
                                price = "0.00", // Pass a placeholder
                                changeAmount = "0.00", // Pass a placeholder
                                changePercentage = "0.00%" // Pass a placeholder
                            )
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun TopMoversContent(
    isLoading: Boolean,
    errorMessage: String?,
    topGainers: List<TopMover>,
    topLosers: List<TopMover>,
    navController: NavHostController,
    onNavigateToTopGainers: () -> Unit,
    onNavigateToTopLosers: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        when {
            isLoading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

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

            else -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (topGainers.isNotEmpty()) {
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            SectionHeader(
                                title = "Top Gainers",
                                onViewAllClicked = onNavigateToTopGainers
                            )
                        }
                        items(topGainers.take(4)) { mover ->
                            TopMoverItem(mover = mover) {
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
                    if (topLosers.isNotEmpty()) {
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            SectionHeader(
                                title = "Top Losers",
                                onViewAllClicked = onNavigateToTopLosers
                            )
                        }
                        items(topLosers.take(4)) { mover ->
                            TopMoverItem(mover = mover) {
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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StocksTopAppBar(
    searchText: String,
    onSearchChanged: (String) -> Unit,
    onClearSearch: () -> Unit
) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color(0xFFE9F9F0) // A very light, pale green
        ),
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "TopMovers",
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    color = Color(0xFF212121), // Dark gray text
                    fontFamily = FontFamily.SansSerif,
                    fontSize = 22.sp,
                    modifier = Modifier.weight(1f)
                )

                TextField(
                    value = searchText,
                    onValueChange = onSearchChanged,
                    placeholder = { Text("Search...", fontSize = 15.sp) },
                    //leadingIcon = { Icon(Icons.Default.Search, "Search Icon") },
                    trailingIcon = {
                        if (searchText.isNotBlank()) {
                            IconButton(onClick = onClearSearch) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear Search")
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(0.7f),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        cursorColor = Color.Black,
                        focusedLeadingIconColor = Color.DarkGray,
                        unfocusedLeadingIconColor = Color.DarkGray,
                        unfocusedPlaceholderColor = Color.Gray,
                        focusedPlaceholderColor = Color.Gray
                    ),
                    singleLine = true
                )
            }
        },
        actions = {}
    )
}

@Composable
fun StocksBottomNav(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavigationBar {
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

        NavigationBarItem(
            label = { Text("Watchlist") },
            icon = {
                Icon(
                    imageVector = Icons.Default.Bookmark,
                    contentDescription = "Add to Watchlist",
                    tint = MaterialTheme.colorScheme.primary
                )
            },
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

@Composable
fun SearchOverlay(
    isSearching: Boolean,
    results: List<SearchResult>,
    onResultClick: (ticker: String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        if (isSearching) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else if (results.isEmpty()) {
            Text(
                "No results found.",
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(16.dp),
                textAlign = TextAlign.Center
            )
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(results) { result ->
                    SearchResultItem(result = result, onClick = { onResultClick(result.symbol) })
                    Divider()
                }
            }
        }
    }
}

@Composable
fun SearchResultItem(result: SearchResult, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(result.symbol, fontWeight = FontWeight.Bold)
            Text(result.name, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        }
        Text(result.region, style = MaterialTheme.typography.bodySmall)
    }
}

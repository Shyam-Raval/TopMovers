package com.example.topmovers.ui.Screens


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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LightMode
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.topmovers.data.model.SearchResult
import com.example.topmovers.data.model.TopMover
import com.example.topmovers.ViewModel.TopMoversViewModel
import com.example.topmovers.navigation.Screens
import com.example.topmovers.ui.components.TopMoverItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreen(
    viewModel: TopMoversViewModel,
    navController: NavHostController,
    useDarkTheme: Boolean,
    onThemeToggle: () -> Unit,
    onNavigateToTopGainers: () -> Unit,
    onNavigateToTopLosers: () -> Unit
) {
    val isLoading = viewModel.isLoading
    val errorMessage = viewModel.errorMessage
    val topGainers = viewModel.topGainers
    val topLosers = viewModel.topLosers
    val searchQuery = viewModel.searchQuery
    val searchResults = viewModel.searchResults
    val isSearching = viewModel.isSearching

    var isSearchActive by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        topBar = {
            if (isSearchActive) {
                SearchTopAppBar(
                    searchText = searchQuery,
                    onSearchChanged = { viewModel.onSearchQueryChanged(it) },
                    onClearSearch = { viewModel.clearSearch() },
                    onCloseSearch = {
                        isSearchActive = false
                        viewModel.clearSearch()
                    }
                )
            } else {
                GreetingTopAppBar(
                    onSearchClick = { isSearchActive = true },
                    useDarkTheme = useDarkTheme,
                    onThemeToggle = onThemeToggle
                )
            }
        },
        bottomBar = { StocksBottomNav(navController = navController) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            TopMoversContent(
                isLoading = isLoading,
                errorMessage = errorMessage,
                topGainers = topGainers,
                topLosers = topLosers,
                navController = navController,
                onNavigateToTopGainers = onNavigateToTopGainers,
                onNavigateToTopLosers = onNavigateToTopLosers
            )

            if (searchQuery.isNotBlank()) {
                SearchOverlay(
                    isSearching = isSearching,
                    results = searchResults,
                    onResultClick = { ticker ->
                        navController.navigate(
                            Screens.StockDetails.createRoute(
                                ticker = ticker,
                                price = "0.00",
                                changeAmount = "0.00",
                                changePercentage = "0.00%"
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
    // Define colors for the "View All" text
    val viewAllGreen = Color(0xFF16A782)
    val viewAllRed = Color(0xFFD50000)

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            isLoading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            errorMessage != null -> Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(16.dp),
                textAlign = TextAlign.Center
            )
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
                                onViewAllClicked = onNavigateToTopGainers,
                                actionTextColor = viewAllGreen // Pass green color
                            )
                        }
                        items(topGainers.take(4)) { mover ->
                            Box(
                                modifier = Modifier
                                    .clip(MaterialTheme.shapes.medium)
                            ) {
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
                    if (topLosers.isNotEmpty()) {
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            SectionHeader(
                                title = "Top Losers",
                                onViewAllClicked = onNavigateToTopLosers,
                                actionTextColor = viewAllRed // Pass red color
                            )
                        }
                        items(topLosers.take(4)) { mover ->
                            Box(
                                modifier = Modifier
                                    .clip(MaterialTheme.shapes.medium)
                            ) {
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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GreetingTopAppBar(
    onSearchClick: () -> Unit,
    useDarkTheme: Boolean,
    onThemeToggle: () -> Unit
) {
    val primaryGreen = Color(0xFF16A782)

    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary, // Green background
            titleContentColor = Color.White,                   // White title
            navigationIconContentColor = Color.White,          // White back arrow
            actionIconContentColor = Color.White               // White bookmark icon
        ),
        title = {
            Text(
                text = "TopMovers",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        },
        actions = {
            IconButton(onClick = onSearchClick) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search Stocks"
                )
            }
            IconButton(onClick = onThemeToggle) {
                Icon(
                    imageVector = if (useDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                    contentDescription = "Toggle Theme"
                )
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchTopAppBar(
    searchText: String,
    onSearchChanged: (String) -> Unit,
    onClearSearch: () -> Unit,
    onCloseSearch: () -> Unit
) {
    val primaryGreen = Color(0xFF16A782)

    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary, // Green background
            titleContentColor = Color.White,                   // White title
            navigationIconContentColor = Color.White,          // White back arrow
            actionIconContentColor = Color.White               // White bookmark icon
        ),
        navigationIcon = {
            IconButton(onClick = onCloseSearch) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Close Search"
                )
            }
        },
        title = {
            TextField(
                value = searchText,
                onValueChange = onSearchChanged,
                placeholder = { Text("Search stocks...") },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = Color.White,
                    focusedPlaceholderColor = Color.White.copy(alpha = 0.7f),
                    unfocusedPlaceholderColor = Color.White.copy(alpha = 0.7f),
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent
                ),
                singleLine = true
            )
        },
        actions = {
            if (searchText.isNotBlank()) {
                IconButton(onClick = onClearSearch) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Clear Search"
                    )
                }
            }
        }
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
                    contentDescription = "Add to Watchlist"
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
    onViewAllClicked: () -> Unit,
    actionTextColor: Color = MaterialTheme.colorScheme.primary // Default color
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
            Text(
                text = "View All",
                color = actionTextColor // Use the passed-in color
            )
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
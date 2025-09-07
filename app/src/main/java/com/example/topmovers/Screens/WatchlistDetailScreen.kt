package com.example.topmovers.Screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.topmovers.ViewModel.WatchlistDetailViewModel
import com.example.topmovers.navigation.Screens
import com.example.topmovers.ui.components.TopMoverItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WatchlistDetailScreen(
    viewModel: WatchlistDetailViewModel,
    navController: NavHostController,
    onBackClicked: () -> Unit
) {
    // 1. Collect the new 'uiState' from the ViewModel
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                // 2. Use the properties from the new 'uiState'
                title = { Text(uiState.watchlistName.ifEmpty { "Loading..." }) },
                navigationIcon = {
                    IconButton(onClick = onBackClicked) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            // 3. The rest of the UI also uses 'uiState'
            when {
                uiState.isLoading -> CircularProgressIndicator()
                uiState.error != null -> Text(uiState.error!!, color = MaterialTheme.colorScheme.error)
                uiState.stocks.isEmpty() -> Text("This watchlist is empty.")
                else -> {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(uiState.stocks) { stock ->
                            TopMoverItem(mover = stock) {
                                navController.navigate(
                                    Screens.StockDetails.createRoute(
                                        ticker = stock.ticker,
                                        price = stock.price,
                                        changePercentage = stock.changePercentage,
                                        changeAmount = stock.changeAmount
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
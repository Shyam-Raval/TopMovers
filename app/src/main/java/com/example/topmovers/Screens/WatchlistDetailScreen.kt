package com.example.topmovers.Screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
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
    // Collect the live data stream from the ViewModel
    val watchlistData by viewModel.watchlistWithStocks.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    // Show the watchlist name as the title, or "Loading..."
                    Text(
                        text = watchlistData?.watchlist?.name ?: "Loading...",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClicked) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            // Show a loading indicator while the data is null
            if (watchlistData == null) {
                CircularProgressIndicator()
            } else if (watchlistData!!.stocks.isEmpty()) {
                // Show a message if the watchlist has no stocks
                Text("This watchlist is empty.")
            } else {
                // Display the grid of stocks
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(watchlistData!!.stocks) { stock ->
                        // Reuse the TopMoverItem composable you already made
                        TopMoverItem(mover = stock) { ticker ->
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
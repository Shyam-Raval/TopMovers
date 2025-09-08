package com.example.topmovers.ui.Screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.outlined.PlaylistAdd
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.example.topmovers.data.model.WatchList
import com.example.topmovers.ViewModel.WatchlistViewModel
import com.example.topmovers.navigation.Screens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WatchlistScreen(
    viewModel: WatchlistViewModel,
    navController: NavHostController
) {
    val watchlists by viewModel.watchlists.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var watchlistToDelete by remember { mutableStateOf<WatchList?>(null) }

    // Define the green color for the TopAppBar
    val topBarGreen = Color(0xFF16A782)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Watchlists", fontWeight = FontWeight.Bold) },
                // --- CHANGE: Reverted TopAppBar to green like the Explore page ---
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary, // Green background
                    titleContentColor = Color.White,                   // White title
                    navigationIconContentColor = Color.White,          // White back arrow
                    actionIconContentColor = Color.White               // White bookmark icon
                ),
                actions = {
                    IconButton(onClick = { showAddDialog = true }) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "Add Watchlist"
                        )
                    }
                }
            )
        },
        bottomBar = { StocksBottomNav(navController = navController) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            if (watchlists.isEmpty()) {
                // The Empty State UI remains the same with the pale purple theme
                EmptyWatchlistState(
                    onExploreClick = {
                        navController.navigate(Screens.Explore.route) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onCreateWatchlistClick = { showAddDialog = true }
                )
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(watchlists) { watchlist ->
                        WatchlistItem(
                            watchlist = watchlist,
                            onClick = {
                                navController.navigate(
                                    Screens.WatchlistDetail.createRoute(watchlist.watchlistId)
                                )
                            },
                            onDelete = { watchlistToDelete = watchlist }
                        )
                    }
                }
            }
        }

        if (showAddDialog) {
            AddWatchlistDialog(
                onDismiss = { showAddDialog = false },
                onSave = { watchlistName ->
                    viewModel.addWatchlist(watchlistName)
                    showAddDialog = false
                }
            )
        }

        watchlistToDelete?.let { watchlist ->
            DeleteConfirmationDialog(
                watchlistName = watchlist.name,
                onConfirm = {
                    viewModel.removeWatchlist(watchlist)
                    watchlistToDelete = null
                },
                onDismiss = {
                    watchlistToDelete = null
                }
            )
        }
    }
}

@Composable
private fun EmptyWatchlistState(
    onExploreClick: () -> Unit,
    onCreateWatchlistClick: () -> Unit
) {
    // This component keeps the improved pale purple theme
    val palePurple = Color(0xFFB39DDB)
    val darkPurpleText = Color(0xFF4527A0)
    val ultraLightPurple = Color(0xFFF3E5F5)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(ultraLightPurple),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.PlaylistAdd,
                contentDescription = "Add to Watchlist",
                tint = palePurple,
                modifier = Modifier.size(60.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Your Watchlist is Empty",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Add stocks to your watchlist to track your favorite companies.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onExploreClick,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = palePurple,
                contentColor = darkPurpleText
            )
        ) {
            Text("Explore Stocks", modifier = Modifier.padding(vertical = 8.dp), fontSize = 16.sp)
        }

        TextButton(onClick = onCreateWatchlistClick) {
            Text("Create Empty Watchlist", color = palePurple, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.Lightbulb,
                contentDescription = "Tip",
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
            Text(
                text = "Tap the bookmark icon on any stock to add it here.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}


// No changes are needed for the composables below this line.

@Composable
fun WatchlistItem(
    watchlist: WatchList,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = watchlist.name,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
        IconButton(onClick = onDelete) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete Watchlist",
                tint = MaterialTheme.colorScheme.error
            )
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = "Go to watchlist"
        )
    }
}

@Composable
fun AddWatchlistDialog(
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var text by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Watchlist") },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                label = { Text("Watchlist Name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (text.isNotBlank()) {
                        onSave(text)
                    }
                },
                enabled = text.isNotBlank()
            ) { Text("Save") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun DeleteConfirmationDialog(
    watchlistName: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Delete Watchlist") },
        text = { Text("Are you sure you want to delete the \"$watchlistName\" watchlist?") },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Delete", color = MaterialTheme.colorScheme.error)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}


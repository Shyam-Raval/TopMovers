package com.example.topmovers.Screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.topmovers.Room.WatchList
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

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Watchlist", fontWeight = FontWeight.Bold) })
        },
        bottomBar = { StocksBottomNav(navController = navController) },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Filled.Add, contentDescription = "Add Watchlist")
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            if (watchlists.isEmpty()) {
                Text(
                    text = "You don't have any watchlists yet.",
                    fontSize = 18.sp,
                    color = Color.Gray
                )
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(watchlists) { watchlist ->
                        WatchlistItem(
                            watchlist = watchlist,
                            onClick = { navController.navigate(
                                Screens.WatchlistDetail.createRoute(watchlist.watchlistId)
                            ) },
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


/**
 * ADDED: The missing Bottom Navigation Bar composable.
 */
//@Composable
//fun StocksBottomNav(navController: NavHostController) {
//    val navBackStackEntry by navController.currentBackStackEntryAsState()
//    val currentDestination = navBackStackEntry?.destination
//
//    NavigationBar {
//        // --- Home Item ---
//        NavigationBarItem(
//            label = { Text("Home") },
//            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
//            selected = currentDestination?.hierarchy?.any { it.route == Screens.Explore.route } == true,
//            onClick = {
//                navController.navigate(Screens.Explore.route) {
//                    popUpTo(navController.graph.findStartDestination().id) {
//                        saveState = true
//                    }
//                    launchSingleTop = true
//                    restoreState = true
//                }
//            }
//        )
//
//        // --- Watchlist Item ---
//        NavigationBarItem(
//            label = { Text("Watchlist") },
//            icon = { Icon(Icons.Default.Star, contentDescription = "Watchlist") },
//            selected = currentDestination?.hierarchy?.any { it.route == Screens.Watchlist.route } == true,
//            onClick = {
//                navController.navigate(Screens.Watchlist.route) {
//                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
//                    launchSingleTop = true
//                    restoreState = true
//                }
//            }
//        )
//    }
//}
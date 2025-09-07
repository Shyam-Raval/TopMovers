package com.example.topmovers.Screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.topmovers.Room.WatchList

@Composable
fun AddToWatchlistDialog(
    existingWatchlists: List<WatchList>,
    onDismiss: () -> Unit,
    onConfirm: (watchlistId: Long?, newWatchlistName: String?) -> Unit
) {
    var selectedWatchlistId by remember { mutableStateOf<Long?>(null) }
    var newWatchlistName by remember { mutableStateOf("") }
    val isConfirmEnabled = selectedWatchlistId != null || newWatchlistName.isNotBlank()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add to Watchlist") },
        text = {
            Column {
                OutlinedTextField(
                    value = newWatchlistName,
                    onValueChange = {
                        newWatchlistName = it
                        if (it.isNotBlank()) { selectedWatchlistId = null }
                    },
                    label = { Text("New Watchlist Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                if (existingWatchlists.isNotEmpty() && newWatchlistName.isBlank()) {
                    Spacer(Modifier.height(16.dp))
                    Text("Or select an existing one:", style = MaterialTheme.typography.titleSmall)
                    LazyColumn(modifier = Modifier.padding(top = 8.dp)) {
                        items(existingWatchlists) { list ->
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .selectable(
                                        selected = (list.watchlistId == selectedWatchlistId),
                                        onClick = { selectedWatchlistId = list.watchlistId }
                                    )
                                    .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = (list.watchlistId == selectedWatchlistId),
                                    onClick = { selectedWatchlistId = list.watchlistId }
                                )
                                Text(text = list.name, modifier = Modifier.padding(start = 8.dp))
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(selectedWatchlistId, newWatchlistName.takeIf { it.isNotBlank() }) },
                enabled = isConfirmEnabled
            ) { Text("Save") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
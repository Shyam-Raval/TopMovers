package com.example.topmovers.ViewModel


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.topmovers.Repository.Repository
import kotlinx.coroutines.launch

// Data class to represent a single watchlist in your database
data class Watchlist(
    val id: Long = 0L, // Use Long for database IDs
    val name: String
)

class WatchlistViewModel(private val repository: Repository) : ViewModel() {

    // --- STATE ---
    // Exposes the list of watchlists for the UI to observe.
    var watchlists by mutableStateOf<List<Watchlist>>(emptyList())
        private set

    // When the ViewModel is first created, load the existing watchlists.
    init {
        loadWatchlists()
    }

    // --- USER ACTIONS ---

    /**
     * Creates a new watchlist and saves it to the database.
     */
    fun addWatchlist(name: String) {
        // Run this operation in a background coroutine
        viewModelScope.launch {
            val newWatchlist = Watchlist(name = name)
            // The repository will handle the database insertion
            // repository.insertWatchlist(newWatchlist)
            // For now, we just add to the local list
            watchlists = watchlists + newWatchlist
        }
    }

    /**
     * Removes a watchlist from the database.
     */
    fun removeWatchlist(watchlist: Watchlist) {
        viewModelScope.launch {
            // The repository will handle the database deletion
            // repository.deleteWatchlist(watchlist)
            // For now, we just remove from the local list
            watchlists = watchlists - watchlist
        }
    }

    /**
     * Loads all watchlists from the repository.
     */
    private fun loadWatchlists() {
        viewModelScope.launch {
            // The repository will get the data from a local Room database
            // watchlists = repository.getAllWatchlists()
        }
    }
}

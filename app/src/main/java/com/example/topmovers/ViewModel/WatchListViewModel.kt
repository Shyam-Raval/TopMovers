package com.example.topmovers.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.topmovers.data.repository.Repository
import com.example.topmovers.data.model.WatchList
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class WatchlistViewModel(private val repository: Repository) : ViewModel() {

    /**
     * This is the corrected property.
     * It's now a StateFlow, which is a live stream of data that your
     * UI can collect with .collectAsState().
     */
    val watchlists: StateFlow<List<WatchList>> = repository.allWatchlists
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addWatchlist(name: String) {
        viewModelScope.launch {
            repository.addWatchlist(name)
        }
    }

    fun removeWatchlist(watchlist: WatchList) {
        viewModelScope.launch {
            repository.removeWatchlist(watchlist)
        }
    }
}
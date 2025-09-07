package com.example.topmovers.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.topmovers.Repository.Repository
import com.example.topmovers.Room.WatchlistWithStocks
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class WatchlistDetailViewModel(
    repository: Repository,
    watchlistId: Long
) : ViewModel() {

    /**
     * A live stream of the specific watchlist and its associated stocks.
     * It starts as null until the first data is loaded from the database.
     */
    val watchlistWithStocks: StateFlow<WatchlistWithStocks?> =
        repository.getWatchlistWithStocks(watchlistId)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = null
            )
}
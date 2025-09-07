package com.example.topmovers.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.topmovers.Repository.Repository
import com.example.topmovers.Retrofit.TopMover
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

// A data class to represent all possible states of your screen
data class WatchlistUiState(
    val isLoading: Boolean = false,
    val stocks: List<TopMover> = emptyList(),
    val error: String? = null,
    val watchlistName: String = ""
)

class WatchlistDetailViewModel(
    private val repository: Repository,
    private val watchlistId: Long
) : ViewModel() {

    private val _uiState = MutableStateFlow(WatchlistUiState(isLoading = true))
    val uiState: StateFlow<WatchlistUiState> = _uiState.asStateFlow()

    init {
        loadFreshWatchlistData()
    }

    private fun loadFreshWatchlistData() {
        viewModelScope.launch {
            _uiState.value = WatchlistUiState(isLoading = true)

            // 1. Get saved tickers from the database
            val localData = repository.getWatchlistWithStocks(watchlistId).first()
            val watchlistName = localData?.watchlist?.name ?: "Watchlist"
            val tickers = localData?.stocks?.map { it.ticker } ?: emptyList()

            if (tickers.isEmpty()) {
                _uiState.value = WatchlistUiState(watchlistName = watchlistName, stocks = emptyList())
                return@launch
            }

            // 2. Fetch fresh data for every ticker from the API
            try {
                coroutineScope {
                    val freshStocks = tickers.map { ticker ->
                        async { repository.getQuoteForTicker(ticker) }
                    }.mapNotNull { it.await().getOrNull() }

                    _uiState.value = WatchlistUiState(watchlistName = watchlistName, stocks = freshStocks)
                }
            } catch (e: Exception) {
                _uiState.value = WatchlistUiState(watchlistName = watchlistName, error = "Failed to load fresh data.")
            }
        }
    }
}
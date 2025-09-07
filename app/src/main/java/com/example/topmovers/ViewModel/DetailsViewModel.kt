package com.example.topmovers.ViewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.topmovers.Repository.Repository
import com.example.topmovers.Retrofit.CompanyInfo
import com.example.topmovers.Retrofit.TopMover
import com.example.topmovers.Room.WatchList
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DetailsViewModel(private val repository: Repository) : ViewModel() {

    // --- State for Stock Details (This part is the same) ---
    var companyInfo by mutableStateOf<CompanyInfo?>(null)
        private set
    var isLoading by mutableStateOf(false)
        private set
    var errorMessage by mutableStateOf<String?>(null)
        private set

    // --- NEW: State and Actions for Watchlists ---

    /**
     * A live stream of all existing watchlists. The popup dialog will use this.
     */
    val allWatchlists: StateFlow<List<WatchList>> = repository.allWatchlists
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /**
     * A live stream that tells us if the current stock is in ANY watchlist.
     * This will be used to change the bookmark icon from empty to filled.
     */
    fun isStockInWatchlist(ticker: String): StateFlow<Boolean> =
        repository.watchlistDao.isStockInWatchlist(ticker)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    /**
     * Action to add the current stock to an existing watchlist.
     */
    fun addStockToWatchlist(stock: TopMover, watchlistId: Long) {
        viewModelScope.launch {
            repository.addStockToWatchlist(stock, watchlistId)
        }
    }

    /**
     * Action to create a new watchlist and add the current stock to it.
     */
    fun createNewWatchlistAndAddStock(name: String, stock: TopMover) {
        viewModelScope.launch {
            val newId = repository.addWatchlist(name)
            if (newId != -1L) { // Room returns -1 on failure
                repository.addStockToWatchlist(stock, newId)
            }
        }
    }

    // --- Data Fetching (This part is the same) ---
    fun fetchStockDetails(ticker: String, apiKey: String) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                companyInfo = repository.getCompanyOverview(ticker, apiKey)
            } catch (e: Exception) {
                errorMessage = "Failed to load stock details: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }
}
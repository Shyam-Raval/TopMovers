package com.example.topmovers.ViewModel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.topmovers.Repository.Repository
import com.example.topmovers.Retrofit.CompanyInfo
import com.example.topmovers.Retrofit.StockDataPoint
import com.example.topmovers.Retrofit.TopMover
import com.example.topmovers.Room.WatchList
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DetailsViewModel(private val repository: Repository) : ViewModel() {

    // --- State for Stock Details ---
    var companyInfo by mutableStateOf<CompanyInfo?>(null)
        private set
    var isLoading by mutableStateOf(false)
        private set
    var errorMessage by mutableStateOf<String?>(null)
        private set

    // --- State for the Chart ---
    var chartData by mutableStateOf<List<StockDataPoint>>(emptyList())
        private set
    var isChartLoading by mutableStateOf(false)
        private set
    var selectedTimeRange by mutableStateOf("1D") // Default is now 1 Day
        private set

    // --- State and Actions for Watchlists ---
    val allWatchlists: StateFlow<List<WatchList>> = repository.allWatchlists
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun isStockInWatchlist(ticker: String): StateFlow<Boolean> =
        repository.watchlistDao.isStockInWatchlist(ticker)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    fun addStockToWatchlist(stock: TopMover, watchlistId: Long) {
        viewModelScope.launch {
            repository.addStockToWatchlist(stock, watchlistId)
        }
    }

    fun createNewWatchlistAndAddStock(name: String, stock: TopMover) {
        viewModelScope.launch {
            val newId = repository.addWatchlist(name)
            if (newId != -1L) {
                repository.addStockToWatchlist(stock, newId)
            }
        }
    }

    // --- Data Fetching Functions ---
    fun fetchStockDetails(ticker: String, apiKey: String) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                companyInfo = repository.getCompanyOverview(ticker, apiKey)
                // Fetch 1D chart data by default when screen loads
                fetchChartData(ticker, "1D")
            } catch (e: Exception) {
                errorMessage = "Failed to load stock details: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun fetchChartData(ticker: String, range: String = "1D") {
        viewModelScope.launch {
            isChartLoading = true
            selectedTimeRange = range

            val function = when (range) {
                "1D" -> "TIME_SERIES_INTRADAY"
                "1W" -> "TIME_SERIES_WEEKLY"
                "1M" -> "TIME_SERIES_MONTHLY"
                "6M" -> "TIME_SERIES_MONTHLY"
                "1Y" -> "TIME_SERIES_MONTHLY"
                else -> "TIME_SERIES_DAILY" // Fallback, though not used by our UI
            }

            try {
                val result = repository.getTimeSeriesData(function, ticker)
                chartData = result.reversed()
                Log.d("ChartDataDebug", "API returned: $result")

            } catch (e: Exception) {
                errorMessage = "Could not load chart data: ${e.message}"
                chartData = emptyList()
            } finally {
                isChartLoading = false
            }
        }
    }
}
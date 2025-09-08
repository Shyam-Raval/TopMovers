package com.example.topmovers.ViewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.topmovers.data.repository.Repository
import com.example.topmovers.data.model.CompanyInfo
import com.example.topmovers.data.model.StockDataPoint
import com.example.topmovers.data.model.TopMover
import com.example.topmovers.data.model.WatchList
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
    var chartErrorMessage by mutableStateOf<String?>(null)
        private set
    var selectedTimeRange by mutableStateOf("1D")
        private set

    // --- State and Actions for Watchlists ---
    val allWatchlists: StateFlow<List<WatchList>> = repository.allWatchlists
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun isStockInWatchlist(ticker: String): StateFlow<Boolean> =
        repository.isStockInWatchlist(ticker)
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
                // Pass the API key to the repository
                companyInfo = repository.getCompanyOverview(ticker, apiKey)
                // Fetch 1D chart data by default when screen loads
                fetchChartData(ticker, "1D", apiKey)
            } catch (e: Exception) {
                errorMessage = "API is exhausted.Please change your IP."
            } finally {
                isLoading = false
            }
        }
    }

    fun fetchChartData(ticker: String, range: String = "1D", apiKey: String) {
        viewModelScope.launch {
            isChartLoading = true
            chartErrorMessage = null
            selectedTimeRange = range

            val function = when (range) {
                "1D" -> "TIME_SERIES_INTRADAY"
                "1W" -> "TIME_SERIES_WEEKLY"
                "1M" -> "TIME_SERIES_MONTHLY"
                "6M" -> "TIME_SERIES_MONTHLY"
                "1Y" -> "TIME_SERIES_MONTHLY"
                else -> "TIME_SERIES_DAILY"
            }

            try {
                // Pass the API key from BuildConfig
                val result = repository.getTimeSeriesData(function, ticker, apiKey)
                chartData = result.reversed()

            } catch (e: Exception) {
                chartErrorMessage = "Data does not exist for this ticker."
                chartData = emptyList()
            } finally {
                isChartLoading = false
            }
        }
    }
}
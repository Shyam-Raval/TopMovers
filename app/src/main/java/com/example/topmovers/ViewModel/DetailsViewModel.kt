package com.example.topmovers.ViewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.topmovers.Repository.Repository
import com.example.topmovers.Retrofit.CompanyInfo
import kotlinx.coroutines.launch

class DetailsViewModel(private val repository: Repository) : ViewModel() {

    // --- STATE ---
    // Holds the detailed company info once fetched.
    var companyInfo by mutableStateOf<CompanyInfo?>(null)
        private set

    // Tracks whether a network request is in progress.
    var isLoading by mutableStateOf(false)
        private set

    // Holds any error message if the network request fails.
    var errorMessage by mutableStateOf<String?>(null)
        private set

    /**
     * Fetches the detailed information for a given stock ticker.
     * Manages the isLoading and errorMessage states.
     *
     * @param ticker The stock symbol to fetch (e.g., "AAPL").
     * @param apiKey Your personal Alpha Vantage API key.
     */
    fun fetchStockDetails(ticker: String, apiKey: String) {
        // Use the ViewModel's coroutine scope to launch a background task.
        viewModelScope.launch {
            isLoading = true
            errorMessage = null // Reset error on new fetch
            try {
                // Call the repository to get the data from the API.
                companyInfo = repository.getCompanyOverview(ticker, apiKey)
            } catch (e: Exception) {
                // If anything goes wrong, capture a user-friendly error message.
                errorMessage = "Failed to load stock details: ${e.message}"
            } finally {
                // This block always runs, ensuring the loading indicator is hidden.
                isLoading = false
            }
        }
    }
}

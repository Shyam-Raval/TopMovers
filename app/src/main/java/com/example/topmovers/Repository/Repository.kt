package com.example.topmovers.Repository

import android.content.Context
import com.example.topmovers.Retrofit.CompanyInfo
import com.example.topmovers.Retrofit.RetrofitInstance
import com.example.topmovers.Retrofit.TopMoversResponse
import com.example.topmovers.ViewModel.Watchlist
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.io.IOException

class ApiLimitException(message: String) : IOException(message)

// NOTE: The Watchlist data class is now defined here or in a separate model file
// so it can be shared across the application.
data class Watchlist(
    val id: Long = 0L,
    val name: String
)

class Repository(context: Context) {

    // --- TEMPORARY IN-MEMORY WATCHLIST SOURCE ---
    // This will be replaced by the Room DAO later.
    private val _watchlists = MutableStateFlow<List<Watchlist>>(emptyList())
    val watchlists = _watchlists.asStateFlow() // Expose as a read-only Flow

    private var nextId = 1L // Simple ID generator for the temporary list

    fun addInMemoryWatchlist(name: String) {
        val newWatchlist = Watchlist(id = nextId++, name = name)
        _watchlists.update { currentList -> currentList + newWatchlist }
    }

    fun removeInMemoryWatchlist(watchlist: Watchlist) {
        _watchlists.update { currentList -> currentList - watchlist }
    }
    // --- END OF TEMPORARY CODE ---


    // --- API Functions ---
    suspend fun getTopMoversFromApi(apiKey: String): TopMoversResponse {
        val response = RetrofitInstance.api.getTopMovers(apiKey = apiKey)
        if (response.information != null) {
            throw ApiLimitException("API rate limit reached. Please try again later.")
        }
        return response
    }

    suspend fun getCompanyOverview(ticker: String, apiKey: String): CompanyInfo {
        return RetrofitInstance.api.getCompanyOverview(symbol = ticker, apiKey = apiKey)
    }
}
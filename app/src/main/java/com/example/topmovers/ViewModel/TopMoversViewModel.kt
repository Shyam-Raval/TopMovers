package com.example.topmovers.ViewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.topmovers.Repository.ApiLimitException
import com.example.topmovers.Repository.Repository
import com.example.topmovers.Retrofit.TopMover
import kotlinx.coroutines.launch
import java.io.IOException
import android.util.Log
import com.example.topmovers.Retrofit.SearchResult
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
class TopMoversViewModel(private val repository: Repository) : ViewModel() {



    // Holds the list of top gainers for the UI.
    var topGainers by mutableStateOf<List<TopMover>>(emptyList())
        private set

    // Holds the list of top losers for the UI.
    var topLosers by mutableStateOf<List<TopMover>>(emptyList())
        private set

    // Holds the list of the most actively traded stocks.
    var mostActivelyTraded by mutableStateOf<List<TopMover>>(emptyList())
        private set

    // Tracks the loading state to show/hide a progress indicator.
    var isLoading by mutableStateOf(false)
        private set

    // Holds any error message to display to the user.
    var errorMessage by mutableStateOf<String?>(null)
        private set

    var searchQuery by mutableStateOf("")
        private set
    var searchResults by mutableStateOf<List<SearchResult>>(emptyList())
        private set
    var isSearching by mutableStateOf(false)
        private set
    private var searchJob: Job? = null


    // --- Initialization ---

    init {
        // Fetch the data as soon as the ViewModel is created.
        fetchTopMovers()
    }


    // --- Public Functions ---
    fun onSearchQueryChanged(query: String) {
        searchQuery = query
        searchJob?.cancel() // Cancel any previous search job

        if (query.isBlank()) {
            searchResults = emptyList()
            isSearching = false
            return
        }

        // Launch a new search job with a debounce delay
        searchJob = viewModelScope.launch {
            isSearching = true
            delay(500L) // Wait for 500ms of user inactivity before calling the API
            try {
                // Call the new repository function
                searchResults = repository.searchTicker(query)
            } catch (e: Exception) {
                Log.e("TopMoversViewModel", "Search failed: ${e.message}")
                searchResults = emptyList() // Clear results on error
            } finally {
                isSearching = false
            }
        }
    }

    fun clearSearch() {
        searchQuery = ""
        searchResults = emptyList()
        isSearching = false
        searchJob?.cancel()
    }


    /**
     * Fetches top movers data from the repository.
     * Manages loading and error states during the API call.
     */
    private fun fetchTopMovers() {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val response = repository.getTopMoversFromApi("NTJBDU9U1JGKA613")

                // Filter lists to remove items with non-numeric prices before updating the state
                topGainers = response.topGainers?.filter {
                    it.price.toDoubleOrNull() != null
                } ?: emptyList()

                topLosers = response.topLosers?.filter {
                    it.price.toDoubleOrNull() != null
                } ?: emptyList()

                mostActivelyTraded = response.mostActivelyTraded?.filter {
                    it.price.toDoubleOrNull() != null
                } ?: emptyList()

            }  catch (e: ApiLimitException) {
                // CATCH our specific API limit exception
                errorMessage ="Api Limit Exhausted. Try from another network."
            }catch (e: IOException) {
                // Handle network errors (e.g., no internet connection).
                errorMessage = "Network error. Please check your connection."
            }  catch (e: Exception) {
                // Handle other potential errors (e.g., API issues, parsing errors).
                errorMessage = "An unexpected error occurred: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }
}

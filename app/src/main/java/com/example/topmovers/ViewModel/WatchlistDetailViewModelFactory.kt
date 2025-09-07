package com.example.topmovers.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.topmovers.Repository.Repository

/**
 * A factory is required to create a ViewModel that has constructor parameters.
 * This factory knows how to create a WatchlistDetailViewModel by providing the
 * repository and the specific watchlistId it needs.
 */
class WatchlistDetailViewModelFactory(
    private val repository: Repository,
    private val watchlistId: Long
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Check if the requested ViewModel is of the correct type.
        if (modelClass.isAssignableFrom(WatchlistDetailViewModel::class.java)) {
            // If it is, create and return an instance of it, passing the necessary arguments.
            @Suppress("UNCHECKED_CAST")
            return WatchlistDetailViewModel(repository, watchlistId) as T
        }
        // If it's not the right type, throw an error.
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
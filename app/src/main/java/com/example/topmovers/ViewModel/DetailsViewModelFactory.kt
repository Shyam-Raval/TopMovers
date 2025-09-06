package com.example.topmovers.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.topmovers.Repository.Repository

class DetailsViewModelFactory(private val repository: Repository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Check if the requested ViewModel is of type DetailsViewModel.
        if (modelClass.isAssignableFrom(DetailsViewModel::class.java)) {
            // If it is, create and return an instance of it, passing the repository.
            @Suppress("UNCHECKED_CAST")
            return DetailsViewModel(repository) as T
        }
        // If it's not the right type, throw an error.
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
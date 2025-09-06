package com.example.topmovers.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.topmovers.Repository.Repository

class TopMoversViewModelFactory(private val repository: Repository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Check if the requested ViewModel class is our TopMoversViewModel
        if (modelClass.isAssignableFrom(TopMoversViewModel::class.java)) {
            // If it is, create and return an instance of it, passing the repository.
            @Suppress("UNCHECKED_CAST")
            return TopMoversViewModel(repository) as T
        }
        // If it's not the class we know how to create, throw an error.
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}

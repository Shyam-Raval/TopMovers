package com.example.topmovers.di

import androidx.room.Room
import com.example.topmovers.Repository.Repository
import com.example.topmovers.Room.AppDatabase
import com.example.topmovers.ViewModel.DetailsViewModel
import com.example.topmovers.ViewModel.TopMoversViewModel
import com.example.topmovers.ViewModel.WatchlistDetailViewModel
import com.example.topmovers.ViewModel.WatchlistViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    // Defines a singleton instance of the AppDatabase
    single {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            "top_movers_database"
        ).build()
    }

    // Defines a singleton instance of the WatchlistDao, getting it from the AppDatabase
    single { get<AppDatabase>().watchlistDao() }

    // Defines a singleton instance of the Repository, providing the WatchlistDao to it
    single { Repository(get()) }
}

val viewModelModule = module {
    // Defines the ViewModels, Koin provides the Repository dependency automatically with get()
    viewModel { TopMoversViewModel(get()) }
    viewModel { WatchlistViewModel(get()) }
    viewModel { DetailsViewModel(get()) }

    // Special definition for WatchlistDetailViewModel that accepts a parameter (the watchlistId)
    viewModel { parameters ->
        WatchlistDetailViewModel(
            repository = get(),
            watchlistId = parameters.get()
        )
    }
}
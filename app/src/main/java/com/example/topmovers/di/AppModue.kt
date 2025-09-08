// File: com/example/topmovers/di/AppModule.kt

package com.example.topmovers.di

import androidx.room.Room
import com.example.topmovers.data.repository.Repository
import com.example.topmovers.data.local.AppDatabase
import com.example.topmovers.data.local.AppDatabase.Companion.MIGRATION_1_2 // NEW: Import migration
import com.example.topmovers.ViewModel.DetailsViewModel
import com.example.topmovers.ViewModel.TopMoversViewModel
import com.example.topmovers.ViewModel.WatchlistDetailViewModel
import com.example.topmovers.ViewModel.WatchlistViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            "top_movers_database"
        )
            .addMigrations(MIGRATION_1_2) // NEW: Add the migration here
            .build()
    }

    single { get<AppDatabase>().watchlistDao() }

    single { Repository(get()) }
}

val viewModelModule = module {
    viewModel { TopMoversViewModel(get()) }
    viewModel { WatchlistViewModel(get()) }
    viewModel { DetailsViewModel(get()) }
    viewModel { parameters ->
        WatchlistDetailViewModel(
            repository = get(),
            watchlistId = parameters.get()
        )
    }
}
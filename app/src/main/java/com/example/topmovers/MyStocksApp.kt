package com.example.topmovers

import android.app.Application
import com.example.topmovers.Repository.Repository
import com.example.topmovers.Room.AppDatabase

// Add parentheses () after Application to call its constructor.
class MyStocksApp : Application() {

    // Using "lazy" so the database is only created when first accessed.
    private val database by lazy { AppDatabase.getDatabase(this) }

    // The repository is created using the DAO from the database.
    val repository by lazy { Repository(database.watchlistDao()) }
}
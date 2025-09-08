package com.example.topmovers

import android.app.Application
import com.example.topmovers.di.appModule
import com.example.topmovers.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

// Add parentheses () after Application to call its constructor.
class MyStocksApp : Application() {

    override fun onCreate() {
        super.onCreate()

        // Start Koin
        startKoin {
            // Log Koin activity (optional but helpful for debugging)
            androidLogger()
            // Provide the Android context to Koin
            androidContext(this@MyStocksApp)
            // Load our modules
            modules(appModule, viewModelModule)
        }
    }
}
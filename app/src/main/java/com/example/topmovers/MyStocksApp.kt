package com.example.topmovers

import android.app.Application
import com.example.topmovers.di.appModule
import com.example.topmovers.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class MyStocksApp : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@MyStocksApp)
            modules(appModule, viewModelModule)
        }
    }
}
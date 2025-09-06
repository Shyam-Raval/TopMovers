package com.example.topmovers.Repository

import android.content.Context
import com.example.topmovers.Retrofit.TopMover
import com.example.topmovers.Retrofit.RetrofitInstance
import com.example.topmovers.Retrofit.TopMoversResponse

 class Repository( context: Context) {
    suspend fun getTopMoversFromApi(apiKey: String): TopMoversResponse {
        // This calls your ApiService and returns the entire response object
        return RetrofitInstance.api.getTopMovers(apiKey = apiKey)
    }
}

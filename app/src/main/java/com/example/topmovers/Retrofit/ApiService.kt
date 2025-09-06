package com.example.topmovers.Retrofit

import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
   @GET("query")
   suspend fun getTopMovers(
      @Query("function")
      function: String = "TOP_GAINERS_LOSERS", // This parameter is fixed for this call

      @Query("apikey")
      apiKey: String // Your API key will be passed in here
   ):TopMoversResponse
}
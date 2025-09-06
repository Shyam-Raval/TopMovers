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
   @GET("query")
   suspend fun getCompanyOverview(
      @Query("function") function: String = "OVERVIEW",
      @Query("symbol") symbol: String, // The stock ticker (e.g., "AAPL")
      @Query("apikey") apiKey: String
   ): CompanyInfo // It will return the CompanyInfo object we defined

}
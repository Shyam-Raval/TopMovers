package com.example.topmovers.data.remote

import com.example.topmovers.data.model.CompanyInfo
import com.example.topmovers.data.model.GlobalQuoteResponse
import retrofit2.http.GET
import retrofit2.http.Query
import com.example.topmovers.data.model.SymbolSearchResponse
import com.example.topmovers.data.model.TimeSeriesResponse
import com.example.topmovers.data.model.TopMoversResponse

interface ApiService {
   @GET("query")
   suspend fun getTopMovers(
      @Query("function")
      function: String = "TOP_GAINERS_LOSERS",

      @Query("apikey")
      apiKey: String
   ): TopMoversResponse

   @GET("query")
   suspend fun getCompanyOverview(
      @Query("function") function: String = "OVERVIEW",
      @Query("symbol") symbol: String,
      @Query("apikey") apiKey: String
   ): CompanyInfo

   @GET("query?function=GLOBAL_QUOTE")
   suspend fun getQuote(
      @Query("symbol") symbol: String,
      @Query("apikey") apiKey: String
   ): GlobalQuoteResponse

   @GET("query")
   suspend fun getTimeSeries(
      @Query("function") function: String,
      @Query("symbol") symbol: String,
      @Query("interval") interval: String? = null,
      @Query("apikey") apiKey: String
   ): TimeSeriesResponse

   @GET("query")
   suspend fun searchSymbol(
      @Query("function") function: String = "SYMBOL_SEARCH",
      @Query("keywords") keywords: String,
      @Query("apikey") apiKey: String
   ): SymbolSearchResponse

}
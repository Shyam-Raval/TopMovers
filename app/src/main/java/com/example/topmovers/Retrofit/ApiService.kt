
package com.example.topmovers.Retrofit

import retrofit2.http.GET
import retrofit2.http.Query
import com.example.topmovers.Retrofit.SymbolSearchResponse // Add this import

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
   @GET("query?function=GLOBAL_QUOTE")
   suspend fun getQuote(
      @Query("symbol") symbol: String,
      @Query("apikey") apiKey: String = "NTJBDU9U1JGKA613"
   ): GlobalQuoteResponse

   // Add this function to your ApiService interface
   @GET("query")
   suspend fun getTimeSeries(
      @Query("function") function: String,
      @Query("symbol") symbol: String,
      @Query("interval") interval: String? = null, // Only needed for Intraday
      @Query("apikey") apiKey: String = "NTJBDU9U1JGKA613" // Your existing key
   ): TimeSeriesResponse // It returns the new data class we just created

   @GET("query")
   suspend fun searchSymbol(
      @Query("function") function: String = "SYMBOL_SEARCH",
      @Query("keywords") keywords: String,
      @Query("apikey") apiKey: String = "NTJBDU9U1JGKA613" // Your existing key
   ): SymbolSearchResponse // Use the new data class as the return type

}

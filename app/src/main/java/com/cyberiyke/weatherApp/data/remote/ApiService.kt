package com.cyberiyke.weatherApp.data.remote

import com.cyberiyke.weatherApp.data.local.model.NewsResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

        @GET("/v2/top-headlines")
        suspend fun getTopHeadline(
            @Query("q") query: String,
            @Query("pageSize") pageSize: Int, // Added default value
            @Query("page") page: Int, // Added default value
        ): retrofit2.Response<NewsResponse>


        // for searching
        @GET("/v2/everything")
        suspend fun getEveryThing(
            @Query("q") query: String,
            @Query("pageSize") pageSize: Int , // Added default value
            @Query("page") page: Int// Added default value
        ): retrofit2.Response<NewsResponse>

}
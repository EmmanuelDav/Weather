package com.cyberiyke.weatherApp.data.remote

import com.cyberiyke.weatherApp.data.local.model.WeatherDataResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("weather")
    suspend fun findCityWeatherData(
        @Query("q") q: String,
        @Query("units") units: String = AppConstants.WEATHER_UNIT,
        @Query("appid") appid: String = BuildConfig.API_KEY
    ): Response<WeatherDataResponse>
}
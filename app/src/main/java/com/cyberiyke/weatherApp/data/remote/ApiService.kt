package com.cyberiyke.weatherApp.data.remote

import com.cyberiyke.weatherApp.BuildConfig
import com.cyberiyke.weatherApp.data.local.model.WeatherDataResponse
import com.cyberiyke.weatherApp.util.AppConstants
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("weather")
    suspend fun findCityWeatherDataByApiCall(
        @Query("q") q: String,
        @Query("units") units: String = AppConstants.WEATHER_UNIT,
        @Query("appid") appid: String = BuildConfig.OPENWEATHER_API_KEY
    ): Response<WeatherDataResponse>
}
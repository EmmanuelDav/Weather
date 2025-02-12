package com.cyberiyke.weatherApp.data.repository

import com.cyberiyke.weatherApp.data.local.model.WeatherDataResponse
import com.cyberiyke.weatherApp.data.local.room.dao.WeatherDao
import com.cyberiyke.weatherApp.data.local.room.entity.Weather
import com.cyberiyke.weatherApp.data.remote.ApiService
import com.cyberiyke.weatherApp.data.remote.SafeApiRequest
import javax.inject.Inject


class WeatherRepository @Inject constructor(
    private val apiService: ApiService,
    private val weatherDao: WeatherDao
) : SafeApiRequest() {

    suspend fun findCityWeatherByApi(cityName: String): WeatherDataResponse {
        return apiRequest {
            apiService.findCityWeatherData(cityName)
        }
    }

    suspend fun addWeather(weatherDetail: Weather) {
        return weatherDao.addWeather(weatherDetail)
    }

    suspend fun fetchWeatherByDd(cityName: String): Weather? {
        return weatherDao.fetchWeatherByCity(cityName)
    }

    suspend fun fetchAllWeatherDetails(): List<Weather> {
        return  weatherDao.fetchAllWeatherDetails()
    }
}
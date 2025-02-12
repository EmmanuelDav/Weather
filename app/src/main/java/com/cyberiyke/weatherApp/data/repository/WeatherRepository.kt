package com.cyberiyke.weatherApp.data.repository

import com.cyberiyke.weatherApp.data.local.room.dao.WeatherDao
import com.cyberiyke.weatherApp.data.remote.ApiService
import javax.inject.Inject


class WeatherRepository @Inject constructor(
    private val apiService: ApiService,
    private val weatherDao: WeatherDao
) {



}
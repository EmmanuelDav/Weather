package com.cyberiyke.weatherApp.data.local.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.cyberiyke.weatherApp.data.local.room.entity.Weather


@Dao
interface WeatherDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addWeather(weatherDetail: Weather)

    @Query("SELECT * FROM ${Weather.TABLE_NAME} WHERE cityName = :cityName")
    suspend fun fetchWeatherByCity(cityName: String): Weather?

    @Query("SELECT * FROM ${Weather.TABLE_NAME}")
    suspend fun fetchAllWeatherDetails(): List<Weather>

}

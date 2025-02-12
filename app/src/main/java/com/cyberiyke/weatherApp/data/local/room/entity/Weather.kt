package com.cyberiyke.weatherApp.data.local.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.cyberiyke.weatherApp.data.local.room.entity.Weather.Companion.TABLE_NAME

@Entity(tableName = TABLE_NAME)
data class Weather(

    @PrimaryKey
    var id: Int? = 0,
    var temp: Double? = null,
    var icon: String? = null,
    var cityName: String? = null,
    var countryName: String? = null,
    var dateTime: String? = null
) {
    companion object {
        const val TABLE_NAME = "weather_detail"
    }
}
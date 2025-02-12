package com.cyberiyke.weatherApp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.cyberiyke.weatherApp.data.local.room.dao.WeatherDao
import com.cyberiyke.weatherApp.data.local.room.entity.Weather

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // migration for adding a new column
        database.execSQL("ALTER TABLE articles ADD COLUMN new_column_name TEXT DEFAULT ''")
    }
}


@Database( entities = [Weather::class], version = 1)
abstract class AppDatabase: RoomDatabase() {

    abstract fun getWeatherDao(): WeatherDao

    companion object {
        const val DATABASE_NAME = "weather_db" // Centralized database name
    }
}
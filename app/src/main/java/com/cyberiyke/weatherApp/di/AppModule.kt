package com.cyberiyke.weatherApp.di

import android.content.Context
import android.content.SharedPreferences
import com.cyberiyke.weatherApp.data.local.room.dao.WeatherDao
import com.cyberiyke.weatherApp.data.remote.ApiService
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [
    DatabaseModule::class,
    NetworkModule::class,
    SharedPreferencesModule::class
])
interface AppComponent {

    // Expose dependencies to the app
    fun provideWeatherDao(): WeatherDao
    fun provideApiService(): ApiService
    fun provideSharedPreferences(): SharedPreferences

    // Factory to create instances of the component
    companion object {
        fun create(context: Context): AppComponent {
            return DaggerAppComponent.builder()
                .applicationContext(context)
                .build()
        }
    }
}
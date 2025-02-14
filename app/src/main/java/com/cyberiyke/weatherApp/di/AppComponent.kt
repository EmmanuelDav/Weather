package com.cyberiyke.weatherApp.di

import android.content.Context
import android.content.SharedPreferences
import com.cyberiyke.weatherApp.data.local.room.dao.WeatherDao
import com.cyberiyke.weatherApp.data.remote.ApiService
import dagger.Component
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AndroidSupportInjectionModule::class,
        DatabaseModule::class,
        NetworkModule::class,
        SharedPreferencesModule::class,
    ContextModule::class
    ]
)
interface AppComponent {

    // Expose dependencies to the app
    fun provideWeatherDao(): WeatherDao
    fun provideApiService(): ApiService
    fun provideSharedPreferences(): SharedPreferences

    // Factory to create instances of the component
    companion object {
        fun create(context: Context): AppComponent {
            return DaggerAppComponent.builder()
                .contextModule(ContextModule(context))
                .build()
        }
    }
}
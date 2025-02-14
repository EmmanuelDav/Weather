package com.cyberiyke.weatherApp.di.component

import android.content.Context
import android.content.SharedPreferences
import com.cyberiyke.weatherApp.data.local.room.dao.WeatherDao
import com.cyberiyke.weatherApp.data.remote.ApiService
import com.cyberiyke.weatherApp.di.module.DatabaseModule
import com.cyberiyke.weatherApp.di.module.NetworkModule
import com.cyberiyke.weatherApp.di.module.SharedPreferencesModule
import com.cyberiyke.weatherApp.di.module.ViewModelModule
import com.cyberiyke.weatherApp.ui.home.HomeFragment
import dagger.BindsInstance
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
        ViewModelModule::class
    ]
)
interface AppComponent {

    fun inject(fragment: HomeFragment)


    // Expose dependencies to the app
    fun provideWeatherDao(): WeatherDao
    fun provideApiService(): ApiService
    fun provideSharedPreferences(): SharedPreferences

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun context(context: Context): Builder
        fun build(): AppComponent
    }
}
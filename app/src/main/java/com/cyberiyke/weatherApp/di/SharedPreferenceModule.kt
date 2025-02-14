package com.cyberiyke.weatherApp.di

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import dagger.Module
import dagger.Provides

@Module
class SharedPreferencesModule {

    @Provides
    fun provideSharedPreferences( context: Context): SharedPreferences {
        return context.getSharedPreferences("ThemePrefs", MODE_PRIVATE)
    }
}
package com.cyberiyke.weatherApp.di

import android.content.Context
import androidx.room.Room
import com.cyberiyke.weatherApp.data.local.AppDatabase
import com.cyberiyke.weatherApp.data.local.MIGRATION_1_2
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, AppDatabase.DATABASE_NAME)
            .addMigrations(MIGRATION_1_2)
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideWeatherDao(database: AppDatabase) = database.getWeatherDao()
}
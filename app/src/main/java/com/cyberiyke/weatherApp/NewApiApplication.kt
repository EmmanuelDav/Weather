package com.cyberiyke.weatherApp

import android.app.Application
import com.cyberiyke.weatherApp.di.AppComponent
import timber.log.Timber


class NewApiApplication: Application(){

    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent.create(this)
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

}

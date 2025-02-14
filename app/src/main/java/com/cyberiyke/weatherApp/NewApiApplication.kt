package com.cyberiyke.weatherApp

import android.app.Application
import com.cyberiyke.weatherApp.di.AppComponent
import com.cyberiyke.weatherApp.di.DaggerAppComponent



class NewApiApplication: Application(){

    companion object {
        const val DEBUG_TAG: String = "Weather_App_TAG"
    }

    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent.create(this)
    }



}

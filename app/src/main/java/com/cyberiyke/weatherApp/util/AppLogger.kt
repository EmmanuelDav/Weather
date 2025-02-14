package com.cyberiyke.weatherApp.util

import android.util.Log
import com.cyberiyke.weatherApp.BuildConfig
import com.cyberiyke.weatherApp.NewApiApplication

class AppLogger {

    companion object {

        private const val TAG = NewApiApplication.DEBUG_TAG

        fun d(message: String) {
            if (BuildConfig.DEBUG) {
                Log.d(TAG, message)
            }
        }

        fun i(message: String) {
            if (BuildConfig.DEBUG) {
                Log.i(TAG, message)
            }
        }

        fun w(message: String) {
            if (BuildConfig.DEBUG) {
                Log.w(TAG, message)
            }
        }

        fun e(message: String) {
            if (BuildConfig.DEBUG) {
                Log.e(TAG, message)
            }
        }
    }
}
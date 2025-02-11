package com.cyberiyke.weatherApp.ui.settings

import android.app.Application
import android.content.Context.MODE_PRIVATE
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.AndroidViewModel


class SettingsViewModel( application: Application) : AndroidViewModel(application) {

    private val _isDarkMode = MutableLiveData<Boolean>()
    val isDarkMode: LiveData<Boolean>
        get() = _isDarkMode

    init {
        // Load the current theme setting from SharedPreferences
        val sharedPreferences = application.getSharedPreferences("ThemePrefs", MODE_PRIVATE)
        _isDarkMode.value = sharedPreferences.getBoolean("isDarkMode", false)
    }

    fun onThemeToggleChanged(isChecked: Boolean) {
        _isDarkMode.value = isChecked
        AppCompatDelegate.setDefaultNightMode(
            if (isChecked) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )
        saveThemeSetting(isChecked)
    }

    private fun saveThemeSetting(isDarkMode: Boolean) {
        val sharedPreferences = getApplication<Application>().getSharedPreferences("ThemePrefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("isDarkMode", isDarkMode)
        editor.apply()
    }
}
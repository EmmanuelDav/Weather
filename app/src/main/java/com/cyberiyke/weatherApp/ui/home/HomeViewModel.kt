package com.cyberiyke.weatherApp.ui.home

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cyberiyke.weatherApp.data.local.model.WeatherDataResponse
import com.cyberiyke.weatherApp.data.local.room.entity.Weather
import com.cyberiyke.weatherApp.util.NetworkResult
import com.cyberiyke.weatherApp.data.repository.WeatherRepository
import com.cyberiyke.weatherApp.util.ApiException
import com.cyberiyke.weatherApp.util.AppConstants
import com.cyberiyke.weatherApp.util.AppUtils
import com.cyberiyke.weatherApp.util.NoInternetException
import com.cyberiyke.weatherApp.util.SingleLiveEvent
import kotlinx.coroutines.Dispatchers

import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


class HomeViewModel @Inject constructor(private val repository: WeatherRepository, private val sharedPreferences: SharedPreferences): ViewModel() {

    private val _weatherLiveData = SingleLiveEvent<NetworkResult<Weather>>()
    val weatherLiveData:LiveData<NetworkResult<Weather>>  = _weatherLiveData

    private val _weatherListData = SingleLiveEvent<NetworkResult<List<Weather>>>()
    val weatherListData:LiveData<NetworkResult<List<Weather>>>  = _weatherListData

    private lateinit var weatherDataResponse : WeatherDataResponse

    private val _isDarkMode = MutableLiveData<Boolean>()
    val isDarkMode: LiveData<Boolean>
        get() = _isDarkMode


    init {
      //  _isDarkMode.value = sharedPreferences.getBoolean("isDarkMode", false)
    }


    private fun findWeatherByCity(city:String){
        _weatherLiveData.value = NetworkResult.loading()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                weatherDataResponse = repository.findCityWeatherByApi(city)
                addWeatherIntoDd(weatherDataResponse)
                viewModelScope.launch(Dispatchers.Main){
                    val weather = Weather()
                    weather.cityName = weatherDataResponse.name.lowercase()
                    weather.icon = weatherDataResponse.weather.first().icon
                    weather.temp = weatherDataResponse.main.temp
                    weather.countryName = weatherDataResponse.sys.country
                    _weatherLiveData.value = NetworkResult.success(weather)
                }

            } catch (e: ApiException) {
                withContext(Dispatchers.Main) {
                    _weatherLiveData.value = NetworkResult.error(e.message ?: "")
                }
            } catch (e: NoInternetException) {
                withContext(Dispatchers.Main) {
                    _weatherLiveData.value = NetworkResult.error(e.message ?: "")
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _weatherLiveData.value = NetworkResult.error(e.message ?: "")
                }
            }
        }
    }

    private suspend fun addWeatherIntoDd(weatherDataResponse: WeatherDataResponse){
        val weather = Weather()
        weather.id = weatherDataResponse.id
        weather.cityName = weatherDataResponse.name.toLowerCase()
        weather.icon = weatherDataResponse.weather.first().icon
        weather.countryName = weatherDataResponse.sys.country
        weather.temp = weatherDataResponse.main.temp
        weather.dateTime = AppUtils.getCurrentDateTime(AppConstants.DATE_FORMAT_1)
        repository.addWeather(weather)
    }


    fun removeFromFavourite(weather: Weather) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.removeFromDB(weather.id ?: return@launch)
        }
    }


    fun fetchWeatherDetailFromDb(cityName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val weatherDetail = repository.fetchWeatherByDd(cityName.toLowerCase())
            withContext(Dispatchers.IO) {
                if (weatherDetail != null) {
                    // Return true of current date and time is greater then the saved date and time of weather searched
                    if (AppUtils.isTimeExpired(weatherDetail.dateTime)) {
                        findWeatherByCity(cityName)
                    } else {
                        _weatherLiveData.postValue(
                                NetworkResult.success(
                                    weatherDetail
                                )
                        )
                    }

                } else {
                    findWeatherByCity(cityName)
                }

            }
        }
    }

    fun fetchAllWeatherDetailsFromDb() {
        viewModelScope.launch(Dispatchers.IO) {
            val weatherDetailList = repository.fetchAllWeatherDetails()
            withContext(Dispatchers.IO) {
                _weatherListData.postValue(
                        NetworkResult.success(weatherDetailList)
                )
            }
        }
    }

    fun onThemeToggleChanged(isChecked: Boolean) {
        _isDarkMode.value = isChecked
        AppCompatDelegate.setDefaultNightMode(
            if (isChecked) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )
        saveThemeSetting(isChecked)
    }

    private fun saveThemeSetting(isDarkMode: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean("isDarkMode", isDarkMode)
        editor.apply()
    }
}


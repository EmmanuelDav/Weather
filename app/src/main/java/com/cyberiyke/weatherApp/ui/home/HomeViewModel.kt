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
import com.shashank.weatherapp.util.Event

import kotlinx.coroutines.launch
import javax.inject.Inject


class HomeViewModel @Inject constructor(private val repository: WeatherRepository, private val sharedPreferences: SharedPreferences): ViewModel() {

    private val _weatherLiveData = MutableLiveData<Event<NetworkResult<Weather>>>()
    val weatherLiveData:LiveData<Event<NetworkResult<Weather>>>  = _weatherLiveData

    private val _weatherListData = MutableLiveData<Event<NetworkResult<List<Weather>>>>()
    val weatherListData:LiveData<Event<NetworkResult<List<Weather>>>>  = _weatherListData

    private lateinit var weatherDataResponse : WeatherDataResponse

    private val _isDarkMode = MutableLiveData<Boolean>()
    val isDarkMode: LiveData<Boolean>
        get() = _isDarkMode


    init {
        _isDarkMode.value = sharedPreferences.getBoolean("isDarkMode", false)
    }


     fun findWeatherByCityApiCall(city:String){
        _weatherLiveData.value = Event(NetworkResult.loading(null))
        viewModelScope.launch {
            try {
                weatherDataResponse = repository.findCityWeatherByApi(city)
                addWeatherIntoDd(weatherDataResponse)
                viewModelScope.launch{
                    val weather = Weather()
                    weather.cityName = weatherDataResponse.name.lowercase()
                    weather.icon = weatherDataResponse.weather.first().icon
                    weather.temp = weatherDataResponse.main.temp
                    weather.countryName = weatherDataResponse.sys.country
                    _weatherLiveData.value = Event(NetworkResult.success(weather))
                }

            } catch (e: ApiException) {
                    _weatherLiveData.value = Event(NetworkResult.error(e.message ?: "",null))
            } catch (e: NoInternetException) {
                    _weatherLiveData.value = Event(NetworkResult.error(e.message ?: "", null))
            } catch (e: Exception) {
                    _weatherLiveData.value = Event(NetworkResult.error(e.message ?: "", null))
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
        viewModelScope.launch {
            repository.deleteFromDbByID(weather.id ?: return@launch)
        }
    }


    fun fetchWeatherDetailFromDb(cityName: String) {
        viewModelScope.launch {
            val weatherDetail = repository.fetchWeatherByCityName(cityName.toLowerCase())
                if (weatherDetail != null) {
                    // Return true of current date and time is greater then the saved date and time of weather searched
                    // if the time is 10 mins old
                    if (AppUtils.isTimeExpired(weatherDetail.dateTime)) {
                        findWeatherByCityApiCall(cityName)
                    } else {
                        _weatherLiveData.postValue(Event(
                                NetworkResult.success(
                                    weatherDetail
                                ))
                        )
                    }

                } else {
                    findWeatherByCityApiCall(cityName)
                }
        }
    }

    fun fetchAllWeatherDetailsFromDb()  = viewModelScope.launch {
            val weatherDetailList = repository.fetchAllWeatherDetails()
                _weatherListData.postValue(
                       Event( NetworkResult.success(weatherDetailList))
                )
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


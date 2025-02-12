package com.cyberiyke.weatherApp.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.cyberiyke.weatherApp.data.local.model.WeatherDataResponse
import com.cyberiyke.weatherApp.data.local.room.entity.Weather
import com.cyberiyke.weatherApp.util.NetworkResult
import com.cyberiyke.weatherApp.data.repository.WeatherRepository
import com.cyberiyke.weatherApp.util.AppConstants
import com.cyberiyke.weatherApp.util.AppUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(private val repository: WeatherRepository): ViewModel() {

    private val _weatherLiveData = MutableLiveData<NetworkResult<Weather>>()
    val weatherLiveData:LiveData<NetworkResult<Weather>>  = _weatherLiveData

    private val _weatherListData = MutableLiveData<NetworkResult<List<Weather>>>()
    val weatherListData:LiveData<NetworkResult<List<Weather>>>  = _weatherListData

    private lateinit var weatherDataResponse : WeatherDataResponse


    fun findWeatherByCity(city:String){
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

            }catch (e: Exception){
                withContext(Dispatchers.Main) {
                    _weatherLiveData.postValue(NetworkResult.error(e.message ?: ""))
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

    fun fetchWeatherDetailFromDb(cityName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val weatherDetail = repository.fetchWeatherByDd(cityName.toLowerCase())
            withContext(Dispatchers.Main) {
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
            withContext(Dispatchers.Main) {
                _weatherListData.postValue(
                        NetworkResult.success(weatherDetailList)
                )
            }
        }
    }

}


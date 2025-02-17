package com.cyberiyke.weatherApp.ui.home

import android.content.SharedPreferences
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.cyberiyke.weatherApp.data.local.model.WeatherDataResponse
import com.cyberiyke.weatherApp.data.local.room.dao.WeatherDao
import com.cyberiyke.weatherApp.data.local.room.entity.Weather
import com.cyberiyke.weatherApp.data.remote.ApiService
import com.cyberiyke.weatherApp.data.repository.WeatherRepository
import com.cyberiyke.weatherApp.util.AppConstants
import com.cyberiyke.weatherApp.util.AppUtils
import com.cyberiyke.weatherApp.util.NetworkResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.argThat
import retrofit2.Response


@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class HomeViewModelTest {

    @Mock
    private lateinit var repository: WeatherRepository

    @Mock
    private lateinit var sharedPreferences: SharedPreferences

    @Mock
    private lateinit var apiService: ApiService

    @Mock
    private lateinit var weatherDao: WeatherDao

    private lateinit var viewModel: HomeViewModel

    private val testDispatcher = StandardTestDispatcher()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        repository = WeatherRepository(apiService, weatherDao)
        viewModel = HomeViewModel(repository, sharedPreferences)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `fetchWeatherDetailFromDb should update LiveData with cached weather data if not expired`() = runTest {
        // Arrange
        val cityName = "Lagos"
        val mockWeather = Weather(
            id = 1,
            cityName = cityName,
            temp = 20.0,
            countryName = "UK",
            dateTime = AppUtils.getCurrentDateTime(AppConstants.DATE_FORMAT_1),
            icon = "01d"
        )

        // Mock the repository behavior
        `when`(repository.fetchWeatherByCityName(cityName)).thenReturn(mockWeather)

        // Mock AppUtils.isTimeExpired to return false (data is not expired)
        `when`(AppUtils.isTimeExpired(mockWeather.dateTime)).thenReturn(false)

        // Act
        val observer = mock(Observer::class.java) as Observer<NetworkResult<Weather>>
        viewModel.weatherLiveData.observeForever(observer)
        viewModel.fetchWeatherDetailFromDb(cityName)

        // Advance coroutines
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        verify(observer).onChanged(NetworkResult.success(mockWeather))
    }

    @Test
    fun `fetchWeatherDetailFromDb should fetch fresh data if cached data is expired`() = runTest {
        // Arrange
        val cityName = "Lagos"
        val mockWeather = Weather(
            id = 1,
            cityName = cityName,
            temp = 20.0,
            countryName = "UK",
            dateTime = AppUtils.getCurrentDateTime(AppConstants.DATE_FORMAT_1),
            icon = "01d"
        )

        val mockWeatherDataResponse = WeatherDataResponse(
            base = "stations",
            clouds = WeatherDataResponse.Clouds(all = 75),
            cod = 200,
            coord = WeatherDataResponse.Coord(lat = 51.5074, lon = -0.1278),
            dt = 1638288000,
            id = 2643743,
            main = WeatherDataResponse.Main(
                feelsLike = 18.0,
                grndLevel = 1012,
                humidity = 81,
                pressure = 1015,
                seaLevel = 1015,
                temp = 20.0,
                tempMax = 21.0,
                tempMin = 19.0
            ),
            name = "London",
            sys = WeatherDataResponse.Sys(
                country = "UK",
                sunrise = 1638249600,
                sunset = 1638283200
            ),
            timezone = 0,
            visibility = 10000,
            weather = listOf(
                WeatherDataResponse.Weather(
                    description = "clear sky",
                    icon = "01d",
                    id = 800,
                    main = "Clear"
                )
            ),
            wind = WeatherDataResponse.Wind(
                deg = 180,
                gust = 5.0,
                speed = 3.0
            )
        )

        // Mock the repository behavior
        `when`(repository.fetchWeatherByCityName(cityName)).thenReturn(mockWeather)
        `when`(repository.findCityWeatherByApi(cityName)).thenReturn(mockWeatherDataResponse)

        // Mock AppUtils.isTimeExpired to return true (data is expired)
        `when`(AppUtils.isTimeExpired(mockWeather.dateTime)).thenReturn(true)

        // Act
        val observer = mock(Observer::class.java) as Observer<NetworkResult<Weather>>
        viewModel.weatherLiveData.observeForever(observer)
        viewModel.fetchWeatherDetailFromDb(cityName)

        // Advance coroutines
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        verify(observer).onChanged(NetworkResult.success(mockWeather))
    }

    @Test
    fun `fetchAllWeatherDetailsFromDb should update LiveData with weather list`() = runTest {
        // Arrange
        val weatherList = listOf(
            Weather(id = 1, cityName = "Lagos", temp = 30.0),
            Weather(id = 2, cityName = "Abuja", temp = 25.0)
        )

        `when`(repository.fetchAllWeatherDetails()).thenReturn(weatherList)

        // Act
        val observer = mock(Observer::class.java) as Observer<NetworkResult<List<Weather>>>
        viewModel.weatherListData.observeForever(observer)
        viewModel.fetchAllWeatherDetailsFromDb()

        // Advance coroutines
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        verify(observer).onChanged(NetworkResult.success(weatherList))
    }
}


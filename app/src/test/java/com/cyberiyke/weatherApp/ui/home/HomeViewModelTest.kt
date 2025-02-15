package com.cyberiyke.weatherApp.ui.home

import android.content.SharedPreferences
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
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
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.*

import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyBoolean
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import retrofit2.Response


@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class HomeViewModelTest {

    // Mock dependencies
    @Mock
    lateinit var repository: WeatherRepository

    @Mock
    lateinit var sharedPreferences: SharedPreferences

    @Mock
    lateinit var apiService: ApiService

    @Mock
    lateinit var weatherDao: WeatherDao

    // Test dispatcher
    private val testDispatcher = StandardTestDispatcher()

    // ViewModel instance
    private lateinit var viewModel: HomeViewModel

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule() // For LiveData testing

    @Before
    fun setup() {
        // Initialize mocks
        MockitoAnnotations.openMocks(this)

        // Set the main dispatcher to the test dispatcher
        Dispatchers.setMain(testDispatcher)

        // Initialize the ViewModel with mocked dependencies
        repository = WeatherRepository(apiService, weatherDao)
        viewModel = HomeViewModel(repository, sharedPreferences)
    }

    @After
    fun tearDown() {
        // Reset the main dispatcher
        Dispatchers.resetMain()
    }

    @Test
    fun `fetchWeatherDetailFromDb should update LiveData with weather data`() = runTest {
        // Arrange
        val mockWeather = Weather().apply {
            cityName = "Lagos"
            icon = "01d"
            temp = 20.0
            countryName = "UK"
            dateTime = AppUtils.getCurrentDateTime(AppConstants.DATE_FORMAT_1) // Ensure this is not expired
        }

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
            name = "Lagos",
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

        val cityName = "Lagos"

        // Mock repository behavior
       // `when`(repository.fetchWeatherByDd(cityName)).thenReturn(mockWeather)
        `when`(apiService.findCityWeatherData(cityName)).thenReturn(Response.success(mockWeatherDataResponse))

        // Act
        val observer = mock(Observer::class.java) as Observer<NetworkResult<Weather>>
        viewModel.weatherLiveData.observeForever(observer)

        viewModel.fetchWeatherDetailFromDb(cityName)

        // Advance the coroutine to complete
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

        // Mock repository behavior
        `when`(repository.fetchAllWeatherDetails()).thenReturn(weatherList)

        // Act
        val observer = mock(Observer::class.java) as Observer<NetworkResult<List<Weather>>>
        viewModel.weatherListData.observeForever(observer)

        viewModel.fetchAllWeatherDetailsFromDb()

        // Advance the coroutine to complete
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        verify(observer).onChanged(NetworkResult.success(weatherList))
    }
}


package com.cyberiyke.weatherApp.data.repository


import com.cyberiyke.weatherApp.data.local.model.WeatherDataResponse
import com.cyberiyke.weatherApp.data.local.room.dao.WeatherDao
import com.cyberiyke.weatherApp.data.local.room.entity.Weather
import com.cyberiyke.weatherApp.data.remote.ApiService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner
import retrofit2.Response

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class WeatherRepositoryTest {

    @Mock
    private lateinit var apiService: ApiService

    @Mock
    private lateinit var weatherDao: WeatherDao

    private lateinit var repository: WeatherRepository

    @Before
    fun setUp() {
        repository = WeatherRepository(apiService, weatherDao)
    }

    @Test
    fun `findCityWeatherByApi should call apiService and return WeatherDataResponse`() = runTest {
        // Arrange
        val cityName = "London"
        val mockResponse = WeatherDataResponse(
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

        val mockitoResponseData = Response.success(mockResponse)

        `when`(apiService.findCityWeatherDataByApiCall(cityName)).thenReturn(mockitoResponseData)

        // Act
        val result = repository.findCityWeatherByApi(cityName)

        // Assert
        verify(apiService).findCityWeatherDataByApiCall(cityName) // Verify API was called
        assertEquals(mockResponse, result) // Better assertion
    }

    @Test
    fun `addWeather should call weatherDao to add weather`() = runTest {
        // Arrange
        val mockWeather = Weather().apply {
            cityName = "London"
            temp = 20.0
            countryName = "UK"
        }

        // Act
        repository.addWeather(mockWeather)

        // Assert
        verify(weatherDao).addWeather(mockWeather)
    }

    @Test
    fun `fetchWeatherByCityName should call weatherDao and return Weather`() = runTest {
        // Arrange
        val mockWeather = Weather().apply {
            cityName = "London"
            temp = 20.0
            countryName = "UK"
        }

        val cityName = "London"

        // Mock DAO
        `when`(weatherDao.fetchWeatherByCity(cityName)).thenReturn(mockWeather)

        // Act
        val result = repository.fetchWeatherByCityName(cityName)

        // Assert
        verify(weatherDao).fetchWeatherByCity(cityName)
        assertEquals(mockWeather, result)
    }

    @Test
    fun `fetchAllWeatherDetails should call weatherDao and return list of Weather`() = runTest {
        // Arrange
        val mockWeatherList = listOf(
            Weather().apply {
                cityName = "London"
                temp = 20.0
                countryName = "UK"
            },
            Weather().apply {
                cityName = "Paris"
                temp = 18.0
                countryName = "FR"
            }
        )

        // Mock DAO
        `when`(weatherDao.fetchAllWeatherDetails()).thenReturn(mockWeatherList)

        // Act
        val result = repository.fetchAllWeatherDetails()

        // Assert
        verify(weatherDao).fetchAllWeatherDetails()
        assertEquals(mockWeatherList, result)
    }

    @Test
    fun `deleteFromDbByID should call weatherDao to delete weather by id`() = runTest {
        // Arrange
        val id = 1

        // Act
        repository.deleteFromDbByID(id)

        // Assert
        verify(weatherDao).deleteWeatherById(id)
    }
}

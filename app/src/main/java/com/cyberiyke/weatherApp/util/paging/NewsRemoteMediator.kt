package com.cyberiyke.weatherApp.util.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.cyberiyke.weatherApp.data.local.AppDatabase
import com.cyberiyke.weatherApp.data.local.room.entity.Weather
import com.cyberiyke.weatherApp.data.remote.ApiService
import com.cyberiyke.weatherApp.util.NetworkResult
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject


@OptIn(ExperimentalPagingApi::class)
class NewsRemoteMediator @Inject constructor (
    private val apiService: ApiService,
    private val database: AppDatabase,
) : RemoteMediator<Int, Weather>() {

    private val _networkResult = MutableStateFlow<NetworkResult>(NetworkResult.Idle)
    var networkResult: MutableStateFlow<NetworkResult> = _networkResult

    var query: String = ""

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, Weather>
    ): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH -> 1
            LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
            LoadType.APPEND -> {
                val lastItem = state.lastItemOrNull()
                    ?: return MediatorResult.Success(endOfPaginationReached = true)
                lastItem.pager + 1
            }
        }

        try {

            val response = apiService.getEveryThing(
                query = query,
                pageSize = state.config.pageSize,
                page = page
            )


            if (!response.isSuccessful) {
                _networkResult.value = NetworkResult.Failure("Error because ${response.code()}")
                return MediatorResult.Error(Exception("Error response: ${response.code()}"))
            }

            val articles = response.body()?.articles ?: emptyList()

            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    database.getWeatherDao().clearNonFavoriteData()
                }

                // Map API response to database entities
                val articleEntities = articles.map { article ->
                    Weather(
                        id = article.url.hashCode(),
                        articleTitle = article.title ?: "",
                        articleDescription = article.description ?: "",
                        articleUrl = article.url ?: "",
                        articleDateTime = article.publishedAt ?: "",
                        articleUrlToImage = article.urlToImage ?: "",
                        articleSource = article.source.name,
                        isFavorite = false,
                        pager = page
                    )
                }
                database.getWeatherDao().insertArticle(articleEntities)
            }
            _networkResult.value = NetworkResult.Success
            return MediatorResult.Success(endOfPaginationReached = articles.isEmpty())
        } catch (e: Exception) {
            _networkResult.value = NetworkResult.Failure("Error because ${e.cause}")
            return MediatorResult.Error(e)
        }

    }
}

package com.cyberiyke.weatherApp.data.repository

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.cyberiyke.weatherApp.data.local.AppDatabase
import com.cyberiyke.weatherApp.data.local.room.dao.ArticleDao
import com.cyberiyke.weatherApp.data.local.room.entity.WeatherEntity
import com.cyberiyke.weatherApp.data.remote.ApiService
import com.cyberiyke.weatherApp.data.remote.NetworkResult
import com.cyberiyke.weatherApp.util.paging.NewsRemoteMediator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import timber.log.Timber
import javax.inject.Inject


@OptIn(androidx.paging.ExperimentalPagingApi::class)
class ArticleRepository @Inject constructor(
    private val apiService: ApiService,
    private val articleDao: ArticleDao,
    private val database: AppDatabase,
    private val remoteMediator: NewsRemoteMediator
) {

    var networkResult: StateFlow<NetworkResult> = remoteMediator.networkResult

    // here we are fetching articles from the api and caching them in room database
    fun getArticles(query: String): Flow<PagingData<WeatherEntity>> {

        remoteMediator.query = query // Dynamically set the query

        val pagingSourceFactory = { database.getArticleDao().getAllArticles() }



        return Pager(
            config = PagingConfig(
                pageSize = 10,
                enablePlaceholders = false
            ),

            remoteMediator = remoteMediator,
            pagingSourceFactory = pagingSourceFactory
        ).flow
    }


    suspend fun searchArticles(
        query: String,
        pageSize: Int = 20,
        page: Int = 1
    ): List<WeatherEntity> {
        // Fetch from the API
        val response = apiService.getEveryThing(query, pageSize, page)

        return try {
            if (response.isSuccessful) {
                response.body()?.articles?.map { article ->
                    WeatherEntity(
                        articleTitle = article.title ?: "",
                        articleDescription = article.description ?: "",
                        articleUrl = article.url ?: "",
                        publisedAt = article.publishedAt ?: "",
                        articleDateTime = article.publishedAt ?: "",
                        articleUrlToImage = article.urlToImage ?: "",
                        articleSource = article.source.name,
                        isFavorite = false,
                        pager = 0
                    )
                } ?: emptyList()
            } else {
                Timber.e("Error: ${response.code()} - ${response.message()}")
                emptyList()
            }
        } catch (e: Exception) {
            Timber.e(e, "Error occurred while searching articles")
            emptyList()
        }
    }


    suspend fun updateFavoriteStatus(articleId: Int, isFavourite: Boolean) {
        articleDao.updateFavoriteStatus(articleId, isFavourite)
    }

    fun getFavouriteArticle(): LiveData<List<WeatherEntity>> {
        return articleDao.getFavoriteArticles()
    }

    suspend fun insertSingle(weatherEntity: WeatherEntity) {
        articleDao.insertArticle(listOf(weatherEntity))
    }

}
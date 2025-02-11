package com.cyberiyke.weatherApp.repository

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.cyberiyke.weatherApp.local.AppDatabase
import com.cyberiyke.weatherApp.local.ArticleDao
import com.cyberiyke.weatherApp.local.ArticleEntity
import com.cyberiyke.weatherApp.network.ApiService
import com.cyberiyke.weatherApp.network.NetworkResult
import com.cyberiyke.weatherApp.paging.NewsRemoteMediator
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
    fun getArticles(query: String): Flow<PagingData<ArticleEntity>> {

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
    ): List<ArticleEntity> {
        // Fetch from the API
        val response = apiService.getEveryThing(query, pageSize, page)

        return try {
            if (response.isSuccessful) {
                response.body()?.articles?.map { article ->
                    ArticleEntity(
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

    fun getFavouriteArticle(): LiveData<List<ArticleEntity>> {
        return articleDao.getFavoriteArticles()
    }

    suspend fun insertSingle(articleEntity: ArticleEntity) {
        articleDao.insertArticle(listOf(articleEntity))
    }

}
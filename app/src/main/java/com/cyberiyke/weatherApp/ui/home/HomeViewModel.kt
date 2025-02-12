package com.cyberiyke.weatherApp.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.cyberiyke.weatherApp.data.local.room.entity.WeatherEntity
import com.cyberiyke.weatherApp.data.remote.NetworkResult
import com.cyberiyke.weatherApp.data.repository.ArticleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(private val repository: ArticleRepository): ViewModel() {

    private val searchQuery = MutableStateFlow("Binance") // Default search query

    val article: Flow<PagingData<WeatherEntity>> = searchQuery
        .flatMapLatest { query ->
            repository.getArticles(query)
        }
        .cachedIn(viewModelScope)

    private val _networkStatus = MutableStateFlow<NetworkResult>(NetworkResult.Idle)
    val networkStatus = _networkStatus.asStateFlow()


    init {
        viewModelScope.launch {
            repository.networkResult.collect { status ->
                _networkStatus.value = status
            }
        }
    }


    private val _searchResults = MutableLiveData<List<WeatherEntity>>() // search results
    val searchResults: LiveData<List<WeatherEntity>> get() = _searchResults


    fun setQuery(query: String) {
        searchQuery.value = query
    }



    fun updateToggle(articleId:Int, isFavourite:Boolean){
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateFavoriteStatus(articleId,isFavourite)
        }
    }

    // this function conducts the seatch based on users input
    fun searchArticles(
        query: String,
        pageSize: Int = 20,
        page: Int = 1
    ) {
        viewModelScope.launch {
            try {
                val results = repository.searchArticles(query, pageSize, page)
                _searchResults.value = results
            } catch (e: Exception) {
                Log.d("TAG", "searchArticles: error ${e.message}")
            } finally {
            }
        }
    }

    fun saveArticleFromSearch(isFavourite: Boolean, article: WeatherEntity){
        viewModelScope.launch (Dispatchers.IO){
            repository.insertSingle(article)
            repository.updateFavoriteStatus(article.id, isFavourite)
        }
    }
}


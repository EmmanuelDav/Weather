package com.cyberiyke.weatherApp.ui.favourite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cyberiyke.weatherApp.data.repository.WeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


class FavouriteViewModel  @Inject constructor(private var repository: WeatherRepository): ViewModel() {

   // var favourite = repository.getFavouriteArticle()

    fun updateToggle(articleId:Int, isFavourite:Boolean){
        viewModelScope.launch(Dispatchers.IO) {
           // repository.updateFavoriteStatus(articleId,isFavourite)
        }
    }

}
package com.cyberiyke.weatherApp.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cyberiyke.weatherApp.R
import com.cyberiyke.weatherApp.data.local.room.entity.Weather
import com.cyberiyke.weatherApp.databinding.LayoutItemNewsSearchBinding
import com.cyberiyke.weatherApp.databinding.WeatherItemBinding
import com.cyberiyke.weatherApp.ui.favourite.FavouriteViewModel
import com.cyberiyke.weatherApp.ui.home.HomeViewModel
import com.cyberiyke.weatherApp.util.AppConstants
import com.cyberiyke.weatherApp.util.AppUtils


/**
 * Created by Emmanuel Iyke on 3/7/2024.
 */

class WeatherSearchAdapter(
    private val viewModel: ViewModel,
    private val listener: ((Weather) -> Unit)? = null
) : RecyclerView.Adapter<WeatherSearchAdapter.HomeViewHolder>() {

    private var mainArticleList = mutableListOf<Weather>()
    private var searchResultsList = mutableListOf<Weather>()
    private var isSearchMode = false

    var articles: List<Weather>
        get() = if (isSearchMode) searchResultsList else mainArticleList
        set(value) {
            mainArticleList = value.toMutableList() // Update main article list
            if (!isSearchMode) {
                notifyDataSetChanged() // Refresh only if not in search mode
            }
        }

    // Method to set search results and switch to search mode
    fun setSearchResults(results: List<Weather>) {
        searchResultsList = results.toMutableList()
        isSearchMode = true
        notifyDataSetChanged()
    }

    fun exitSearchMode() {
        isSearchMode = false
        searchResultsList.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val view = WeatherItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HomeViewHolder(view)
    }

    override fun getItemCount() = articles.size

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        holder.bindItems(articles[position])
    }

    inner class HomeViewHolder(private val binding: WeatherItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bindItems(weatherDetail: Weather) {
            binding.apply {
                val iconCode = weatherDetail.icon?.replace("n", "d")
                AppUtils.setGlideImage(
                    imageWeatherSymbol,
                    AppConstants.WEATHER_API_IMAGE_ENDPOINT + "${iconCode}@4x.png"
                )
                textCityName.text =
                    "${weatherDetail.cityName?.capitalize()}, ${weatherDetail.countryName}"
                textTemperature.text = weatherDetail.temp.toString()
                textDateTime.text = weatherDetail.dateTime
            }
        }
    }

    private fun updateFavoriteIcon(isFavorite: Boolean, binding: LayoutItemNewsSearchBinding) {
        binding.favoriteButton.icon = ContextCompat.getDrawable(
            binding.root.context,
            if (isFavorite) R.drawable.baseline_favorite_24 else R.drawable.baseline_favorite_border_24
        )
    }
}



package com.cyberiyke.weatherApp.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import com.cyberiyke.weatherApp.R
import com.cyberiyke.weatherApp.data.local.room.entity.Weather
import com.cyberiyke.weatherApp.databinding.WeatherItemBinding
import com.cyberiyke.weatherApp.util.AppConstants
import com.cyberiyke.weatherApp.util.AppUtils


/**
 * Created by Emmanuel Iyke on 3/7/2024.
 */

class WeatherSearchAdapter (
    private val listener: ((Weather) -> Unit)? = null

): RecyclerView.Adapter<WeatherSearchAdapter.HomeViewHolder>() {

    private var weatherList = mutableListOf<Weather>()

    fun setData(newWeatherDetail: List<Weather>) {
        weatherList.clear()
        weatherList.addAll(newWeatherDetail)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val view = WeatherItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HomeViewHolder(view)
    }

    override fun getItemCount() = weatherList.size

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        holder.bindItems(weatherList[position])
    }

    inner class HomeViewHolder(private val binding: WeatherItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bindItems(weatherDetail: Weather) {
            binding.apply {
                root.setOnClickListener {
                    listener?.invoke(weatherDetail)
                }
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

//    private fun updateFavoriteIcon(isFavorite: Boolean, binding: WeatherItemBinding) {
//        binding.favoriteButton.icon = ContextCompat.getDrawable(
//            binding.root.context,
//            if (isFavorite) R.drawable.star_icon_filled else R.drawable.star_icon
//        )
//    }
}



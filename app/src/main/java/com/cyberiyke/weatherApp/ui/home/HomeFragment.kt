package com.cyberiyke.weatherApp.ui.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.cyberiyke.weatherApp.R
import com.cyberiyke.weatherApp.databinding.FragmentHomeBinding
import com.cyberiyke.weatherApp.ui.dialog.ProgressDialog
import com.cyberiyke.weatherApp.util.AppConstants
import com.cyberiyke.weatherApp.util.AppUtils
import com.cyberiyke.weatherApp.util.NetworkResult
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    private val binding get() = _binding!!
    private lateinit var homeViewModel: HomeViewModel


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val progressDialog = ProgressDialog(requireContext())

    }


    private fun setupUI() {
        initializeRecyclerView()
        binding.inputFindCityWeather.setOnEditorActionListener { view, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                homeViewModel.fetchWeatherDetailFromDb((view as EditText).text.toString())
                homeViewModel.fetchAllWeatherDetailsFromDb()
            }
            false
        }
    }

    private fun initializeRecyclerView() {
        customAdapterSearchedCityTemperature = CustomAdapterSearchedCityTemperature()
        val mLayoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        binding.recyclerViewSearchedCityTemperature.apply {
            layoutManager = mLayoutManager
            itemAnimator = DefaultItemAnimator()
            adapter = customAdapterSearchedCityTemperature
        }
    }

    @SuppressLint("SetTextI18n")
    private fun observeAPICall() {
        homeViewModel.weatherLiveData.observe(this, Observer { state ->
            when (state) {
                is NetworkResult.Error -> {}
                is NetworkResult.Loading -> {}
                is NetworkResult.Success -> {
                    binding.textLabelSearchForCity.hide()
                    binding.imageCity.hide()
                    binding.constraintLayoutShowingTemp.show()
                    binding.inputFindCityWeather.text?.clear()
                    state.data.let { weatherDetail ->
                        val iconCode = weatherDetail.icon?.replace("n", "d")
                        AppUtils.setGlideImage(
                            binding.imageWeatherSymbol,
                            AppConstants.WEATHER_API_IMAGE_ENDPOINT + "${iconCode}@4x.png"
                        )
                        changeBgAccToTemp(iconCode)
                        binding.textTodaysDate.text =
                            AppUtils.getCurrentDateTime(AppConstants.DATE_FORMAT)
                        binding.textTemperature.text = weatherDetail.temp.toString()
                        binding.textCityName.text = "${weatherDetail.cityName?.capitalize()}, ${weatherDetail.countryName}"
                    }
                }
            }
        })
    }

    private fun changeBgAccToTemp(iconCode: String?) {
        when (iconCode) {
            "01d", "02d", "03d" -> binding.imageWeatherHumanReaction.setImageResource(R.drawable.sunny_day)
            "04d", "09d", "10d", "11d" -> binding.imageWeatherHumanReaction.setImageResource(R.drawable.raining)
            "13d", "50d" -> binding.imageWeatherHumanReaction.setImageResource(R.drawable.snowfalling)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
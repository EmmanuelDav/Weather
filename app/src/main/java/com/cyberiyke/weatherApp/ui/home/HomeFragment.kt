package com.cyberiyke.weatherApp.ui.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.cyberiyke.weatherApp.R
import com.cyberiyke.weatherApp.databinding.FragmentHomeBinding
import com.cyberiyke.weatherApp.ui.adapter.WeatherSearchAdapter
import com.cyberiyke.weatherApp.ui.dialog.ProgressDialog
import com.cyberiyke.weatherApp.util.AppConstants
import com.cyberiyke.weatherApp.util.AppUtils
import com.cyberiyke.weatherApp.util.NetworkResult
import com.cyberiyke.weatherApp.util.hide
import com.cyberiyke.weatherApp.util.show
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    private val binding get() = _binding!!
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var weatherSearchAdapter:WeatherSearchAdapter

    private lateinit var progressDialog:ProgressDialog

    private  var darkModeIsChecked = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        homeViewModel.fetchWeatherDetailFromDb("Lagos")
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        observeAPICall()
        homeViewModel.fetchAllWeatherDetailsFromDb()

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

        binding.swipeRefresh.setOnRefreshListener {

            binding.swipeRefresh.isRefreshing = true
            progressDialog.show()


            homeViewModel.fetchWeatherDetailFromDb("Washington")
            homeViewModel.fetchAllWeatherDetailsFromDb()
        }

        binding.darkMode.setOnClickListener {
            homeViewModel.onThemeToggleChanged(darkModeIsChecked)
            darkModeIsChecked = !darkModeIsChecked
        }


    }

    private fun initializeRecyclerView() {
        weatherSearchAdapter = WeatherSearchAdapter()
        progressDialog = ProgressDialog(requireContext())
        val mLayoutManager = GridLayoutManager(requireContext(), 5, GridLayoutManager.HORIZONTAL, false)
        binding.recyclerViewSearchedCityTemperature.apply {
            layoutManager = mLayoutManager
            itemAnimator = DefaultItemAnimator()
            adapter = weatherSearchAdapter
        }
    }

    @SuppressLint("SetTextI18n")
    private fun observeAPICall() {
        homeViewModel.weatherLiveData.observe(viewLifecycleOwner, Observer { state ->
            when (state) {
                is NetworkResult.Error -> {
                    progressDialog.dismiss()
                    Log.d("Issue", "observeAPICall: ${state.message}")


                }
                is NetworkResult.Loading -> {
                    progressDialog.show()
                }
                is NetworkResult.Success -> {
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
                    progressDialog.dismiss()
                    binding.swipeRefresh.isRefreshing = false

                }
            }
        })

        homeViewModel.weatherListData.observe(viewLifecycleOwner, Observer { state ->
            when (state) {
                is NetworkResult.Loading -> {

                }
                is NetworkResult.Success -> {
                    if (state.data.isEmpty()) {
                        binding.recyclerViewSearchedCityTemperature.hide()
                    } else {
                        binding.recyclerViewSearchedCityTemperature.show()
                        weatherSearchAdapter.setData(state.data)
                        binding.swipeRefresh.isRefreshing = false
                    }
                }
                is NetworkResult.Error -> {

                }
            }
        })

        homeViewModel.isDarkMode.observe(viewLifecycleOwner){ isdark ->
            if (isdark) {
                binding.darkMode.setIconResource(R.drawable.dark_mode_24dp)
            } else {
                binding.darkMode.setIconResource(R.drawable.light_mode)
            }
        }
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
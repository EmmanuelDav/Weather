package com.cyberiyke.weatherApp.ui.home.newsitem

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cyberiyke.weatherApp.databinding.FragmentNewsItemBinding

class NewsItemFragment : Fragment() {
    private lateinit var binding:FragmentNewsItemBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNewsItemBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val url = arguments?.getString("url")

        url?.let {
            binding.webView.settings.javaScriptEnabled = true
            binding.webView.loadUrl(it)
        }

    }
}
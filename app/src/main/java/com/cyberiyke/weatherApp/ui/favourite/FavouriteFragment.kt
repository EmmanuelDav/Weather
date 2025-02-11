package com.cyberiyke.weatherApp.ui.favourite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.cyberiyke.weatherApp.R
import com.cyberiyke.weatherApp.databinding.FragmentFavouriteBinding
import com.cyberiyke.weatherApp.ui.adapter.ArticleSearchAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FavouriteFragment : Fragment() {

    private var _binding: FragmentFavouriteBinding? = null
    private lateinit var favouriteViewModel:FavouriteViewModel
    private lateinit var homeAdapter: ArticleSearchAdapter



    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
         favouriteViewModel =
            ViewModelProvider(this)[FavouriteViewModel::class.java]

        _binding = FragmentFavouriteBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    private fun clickListener(){
         homeAdapter = ArticleSearchAdapter(favouriteViewModel, {
            val bundle = Bundle().apply {
                putString("url", it.articleUrl) // Pass the article URL
            }
            findNavController().navigate(R.id.action_navigation_dashboard_to_newsItemFragment, bundle)
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        favouriteViewModel.favourite.observe(viewLifecycleOwner) { news ->
            binding.rv.layoutManager = LinearLayoutManager(activity)
            binding.rv.adapter = homeAdapter
            if (news != null) {
                homeAdapter.articles = news.toMutableList()
            }
        }

        clickListener()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
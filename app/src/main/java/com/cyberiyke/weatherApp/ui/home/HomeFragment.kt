package com.cyberiyke.weatherApp.ui.home

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cyberiyke.weatherApp.R
import com.cyberiyke.weatherApp.databinding.FragmentHomeBinding
import com.cyberiyke.weatherApp.network.NetworkResult
import com.cyberiyke.weatherApp.ui.MainActivity
import com.cyberiyke.weatherApp.ui.adapter.ArticleSearchAdapter
import com.cyberiyke.weatherApp.ui.adapter.NewsLoadStateAdapter
import com.cyberiyke.weatherApp.ui.adapter.NewsPagingAdapter
import com.cyberiyke.weatherApp.ui.dialog.ProgressDialog
import com.google.android.material.search.SearchView.TransitionState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    private val binding get() = _binding!!
    private lateinit var homeViewModel: HomeViewModel


    private lateinit var homeAdapter: ArticleSearchAdapter
    private lateinit var adapter:NewsPagingAdapter


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        homeViewModel =
            ViewModelProvider(this)[HomeViewModel::class.java]

        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        (activity as AppCompatActivity).setSupportActionBar(binding.searchBar)


        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = NewsPagingAdapter()

        binding.rvMain.adapter = adapter.withLoadStateFooter(
            footer = NewsLoadStateAdapter {adapter.retry()}
        )

        lifecycleScope.launch {
            homeViewModel.article.collectLatest { pagingData ->
                adapter.submitData(pagingData)
            }
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            adapter.refresh()
            binding.swipeRefreshLayout.isRefreshing = false
        }

        val progressDialog = ProgressDialog(requireContext())

        lifecycleScope.launch {
            homeViewModel.networkStatus.collect{ networkResult ->
                when(networkResult){
                    is NetworkResult.Idle ->{
                        Log.d("TAG", "onViewCreated: Loading")
                        progressDialog.show()
                    }
                    is NetworkResult.Failure -> {
                        Log.d("TAG", "onViewCreated: error ${networkResult.message}")
                        Toast.makeText(
                            requireContext(),
                            "Error: ${networkResult.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                        progressDialog.dismiss()
                    }
                    is NetworkResult.Success ->{
                        Log.d("TAG", "onViewCreated: Success")
                        progressDialog.dismiss()
                    }
                }
            }
        }


        adapter.onItemClickListener = { articleEntity ->
            val bundle = Bundle().apply {
                putString("url", articleEntity.articleUrl) // Pass the article URL
            }
            findNavController().navigate(R.id.action_navigation_home_to_newsItemFragment, bundle)
        }

        adapter.onFavoriteToggle = { articleId, isfavourited ->
            homeViewModel.updateToggle(articleId, isfavourited)

        }

        homeAdapter = ArticleSearchAdapter(homeViewModel)



        homeViewModel.searchResults.observe(viewLifecycleOwner){ results ->
            binding.rvSearch.layoutManager = LinearLayoutManager(activity)
            binding.rvSearch.adapter = homeAdapter

            if (results != null) homeAdapter.setSearchResults(results.toMutableList())

        }

        binding.searchView.addTransitionListener { searchView, previousState, newState ->
            when(newState){
                TransitionState.SHOWING ->   (activity as MainActivity).setBottomNavigationVisibility(false)
                TransitionState.HIDING -> (activity as MainActivity).setBottomNavigationVisibility(true)
                TransitionState.HIDDEN -> (activity as MainActivity).setBottomNavigationVisibility(true)
                TransitionState.SHOWN -> (activity as MainActivity).setBottomNavigationVisibility(false)
            }
        }

        // Check if the RecyclerView is scrolled to the top
        // Then refresh
        binding.rvSearch.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val isAtTop = !recyclerView.canScrollVertically(-1)
                // Enable swipe refresh only when the RecyclerView is at the top
                binding.swipeRefreshLayout.isEnabled = isAtTop
            }
        })
        binding.rvMain.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val isAtTop = !recyclerView.canScrollVertically(-1)
                // Enable swipe refresh only when the RecyclerView is at the top
                binding.swipeRefreshLayout.isEnabled = isAtTop
            }
        })

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (binding.searchView.isShowing) {
                        binding.searchView.hide()
                    } else {
                        isEnabled = false
                        requireActivity().onBackPressed()
                    }
                }
            }
        )
        searchFromApi()
    }


    private fun searchFromApi(){
        binding.searchView.editText.apply {

            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {}
            })

            setOnEditorActionListener { _, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_DONE || (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER
                            && event.action == KeyEvent.ACTION_DOWN)) {

                    val query = text.toString()
                    homeViewModel.searchArticles(query)
                    true
                } else {
                    false
                }
            }
        }



    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



}
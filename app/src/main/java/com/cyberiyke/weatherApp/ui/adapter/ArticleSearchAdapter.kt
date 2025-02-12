package com.cyberiyke.weatherApp.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cyberiyke.weatherApp.R
import com.cyberiyke.weatherApp.data.local.ArticleEntity
import com.cyberiyke.weatherApp.databinding.LayoutItemNewsSearchBinding
import com.cyberiyke.weatherApp.ui.favourite.FavouriteViewModel
import com.cyberiyke.weatherApp.ui.home.HomeViewModel


/**
 * Created by Emmanuel Iyke on 3/7/2024.
 */
class ArticleSearchAdapter(
    private val viewModel: ViewModel,
    private val listener: ((ArticleEntity) -> Unit)? = null
) : RecyclerView.Adapter<ArticleSearchAdapter.HomeViewHolder>() {

    private var mainArticleList = mutableListOf<ArticleEntity>()
    private var searchResultsList = mutableListOf<ArticleEntity>()
    private var isSearchMode = false

    var articles: List<ArticleEntity>
        get() = if (isSearchMode) searchResultsList else mainArticleList
        set(value) {
            mainArticleList = value.toMutableList() // Update main article list
            if (!isSearchMode) {
                notifyDataSetChanged() // Refresh only if not in search mode
            }
        }

    // Method to set search results and switch to search mode
    fun setSearchResults(results: List<ArticleEntity>) {
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
        val view = LayoutItemNewsSearchBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HomeViewHolder(view)
    }

    override fun getItemCount() = articles.size

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        holder.bind(articles[position])
    }

    inner class HomeViewHolder(private val binding: LayoutItemNewsSearchBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(article: ArticleEntity) = with(itemView) {
            binding.articleTitle.text = article.articleTitle
            binding.articleDescription.text = article.articleDescription
            binding.articleDateTime.text = article.publisedAt
            binding.articleSource.text = article.articleSource
            updateFavoriteIcon(article.isFavorite, binding)
            Glide.with(this)
                .load(article.articleUrlToImage)
                .placeholder(R.drawable.img_placeholder)
                .error(R.drawable.img_placeholder)
                .into(binding.articleImage)
            setOnClickListener {
                listener?.invoke(article)
            }
            binding.favoriteButton.setOnClickListener {
                val newFavState = !article.isFavorite
                article.isFavorite = newFavState
                updateFavoriteIcon(newFavState, binding)

                when (viewModel) {
                    is HomeViewModel -> viewModel.updateToggle(article.id, newFavState)
                    is FavouriteViewModel -> viewModel.updateToggle(article.id, newFavState)
                }

                if (isSearchMode && viewModel is HomeViewModel) {
                    viewModel.saveArticleFromSearch(newFavState, article)
                }
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



package com.cyberiyke.weatherApp.ui.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cyberiyke.weatherApp.R
import com.cyberiyke.weatherApp.databinding.ItemNewsBinding
import com.cyberiyke.weatherApp.data.local.room.entity.WeatherEntity

class NewsPagingAdapter : PagingDataAdapter<WeatherEntity, NewsPagingAdapter.NewsViewHolder>(WeatherEntityComparator) {

    var onFavoriteToggle: ((Int, Boolean) -> Unit)? = null
    var onItemClickListener: ((WeatherEntity) -> Unit)? = null

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val article = getItem(position)
        article?.let { holder.bind(it) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val binding = ItemNewsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NewsViewHolder(binding)
    }

    companion object {
        val WeatherEntityComparator = object : DiffUtil.ItemCallback<WeatherEntity>() {
            override fun areItemsTheSame(oldItem: WeatherEntity, newItem: WeatherEntity): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: WeatherEntity, newItem: WeatherEntity): Boolean {
                return oldItem == newItem
            }
        }
    }

    inner class NewsViewHolder(private val binding: ItemNewsBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(article: WeatherEntity) {
            binding.articleTitle.text = article.articleTitle
            binding.articleDescription.text = article.articleDescription
            binding.articleDateTime.text = article.publisedAt
            binding.articleSource.text = article.articleSource
            updateFavoriteIcon(article.isFavorite, binding)
            Glide.with(binding.root.context)
                .load(article.articleUrlToImage)
                .placeholder(R.drawable.img_placeholder)
                .error(R.drawable.img_placeholder)
                .into(binding.articleImage)

            binding.favoriteButton.setOnClickListener {
                val newFavState = !article.isFavorite
                onFavoriteToggle?.invoke(article.id, newFavState)
            }

            binding.root.setOnClickListener {
                onItemClickListener?.invoke(article)
            }
        }
    }

    private fun updateFavoriteIcon(isFavorite: Boolean, binding: ItemNewsBinding) {
        binding.favoriteButton.icon = ContextCompat.getDrawable(
            binding.root.context,
            if (isFavorite) R.drawable.baseline_favorite_24 else R.drawable.baseline_favorite_border_24
        )
    }
}

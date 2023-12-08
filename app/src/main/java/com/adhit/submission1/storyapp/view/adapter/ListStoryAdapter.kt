package com.adhit.submission1.storyapp.view.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.adhit.submission1.storyapp.data.response.ListStoryItem
import com.adhit.submission1.storyapp.databinding.ItemRvStoryBinding
import com.adhit.submission1.storyapp.view.detail.DetailActivity
import com.bumptech.glide.Glide

class ListStoryAdapter :
    PagingDataAdapter<ListStoryItem, ListStoryAdapter.StoryViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val binding = ItemRvStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        val item = getItem(position)
        item?.let { holder.bind(it) }
    }

    inner class StoryViewHolder(private val binding: ItemRvStoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ListStoryItem) {
            binding.tvTitle.text = item.name
            binding.tvDescription.text = item.description
            Glide.with(binding.root.context)
                .load(item.photoUrl)
                .into(binding.ivPhoto)

            val optionsCompat: ActivityOptionsCompat =
                ActivityOptionsCompat.makeSceneTransitionAnimation(
                    itemView.context as Activity,
                    Pair(binding.ivPhoto, "photo"),
                    Pair(binding.tvTitle, "title"),
                    Pair(binding.tvDescription, "description"),
                )

            itemView.setOnClickListener {
                val intent = Intent(itemView.context, DetailActivity::class.java)
                intent.putExtra("ID", item.id)
                itemView.context.startActivity(intent, optionsCompat.toBundle())
            }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: ListStoryItem,
                newItem: ListStoryItem
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}
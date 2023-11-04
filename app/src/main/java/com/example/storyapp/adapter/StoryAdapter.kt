package com.example.storyapp.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.storyapp.databinding.ItemUserBinding
import com.example.storyapp.ui.login.detailStory.StoryDetailActivity
import com.example.storyapp.retrofit.ListStoryItem
import dateFormatter
import java.util.TimeZone

class StoryAdapter : PagingDataAdapter<ListStoryItem, StoryAdapter.MyViewHolder>(DIFF_CALBACK) {

    inner class MyViewHolder(private val binding: ItemUserBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(result: ListStoryItem) {
            Glide.with(itemView.context)
                .load(result.photoUrl).skipMemoryCache(true)
                .into(binding.storyImageView)
            binding.tvItemTitle.text = result.name
            binding.tvItemDescription.text = result.description
            binding.tvItemDate.text = dateFormatter(result.createdAt, TimeZone.getDefault().id)

            binding.root.setOnClickListener {
                val detailIntent = Intent(binding.root.context, StoryDetailActivity::class.java)
                detailIntent.putExtra(StoryDetailActivity.EXTRA_TITLE, result.name)
                detailIntent.putExtra(StoryDetailActivity.EXTRA_DESC, result.description)
                detailIntent.putExtra(StoryDetailActivity.EXTRA_DATE, result.createdAt)
                detailIntent.putExtra(StoryDetailActivity.EXTRA_THUMBNAIL, result.photoUrl)
                binding.root.context.startActivity(detailIntent)

            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val result = getItem(position)
        if (result != null) {
            holder.bind(result)
        }
    }

    companion object {
        val DIFF_CALBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem
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






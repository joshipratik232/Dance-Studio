package com.example.apptest.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.apptest.databinding.CategoryVideoBinding
import com.example.apptest.model.categoryHome.Video

class CategoryVideoAdapter(private val onclickListener: OnclickListener) : ListAdapter<Video, CategoryVideoAdapter.DataViewHolder>(DiffCallBack) {
    class DataViewHolder(private val binding: CategoryVideoBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(video: Video?) {
            binding.video = video
            binding.executePendingBindings()
        }
    }

    companion object DiffCallBack : DiffUtil.ItemCallback<Video>() {
        override fun areItemsTheSame(oldItem: Video, newItem: Video): Boolean {
            return oldItem.VideoId === newItem.VideoId
        }

        override fun areContentsTheSame(oldItem: Video, newItem: Video): Boolean {
            return oldItem.VideoId == newItem.VideoId
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder {
        return DataViewHolder(CategoryVideoBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        val video = getItem(position)
        holder.itemView.setOnClickListener {
            onclickListener.onclick(video)
        }
        holder.bind(video)
    }

    class OnclickListener(val clickListener: (video: Video) -> Unit) {
        fun onclick(video: Video) = clickListener(video)
    }
}
package com.example.apptest.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.apptest.databinding.SeeAllGridVideoBinding
import com.example.apptest.model.categorySeeAll.CatWiseVideoData

class CatWiseSeeAllAdapter(private val onclickListener: OnclickListener) : ListAdapter<CatWiseVideoData, CatWiseSeeAllAdapter.DataViewHolder>(DiffCallBack) {
    class DataViewHolder(private val binding: SeeAllGridVideoBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(catWiseVideoData: CatWiseVideoData) {
            binding.catData = catWiseVideoData
            binding.executePendingBindings()
        }
    }

    companion object DiffCallBack : DiffUtil.ItemCallback<CatWiseVideoData>() {
        override fun areItemsTheSame(oldItem: CatWiseVideoData, newItem: CatWiseVideoData): Boolean {
            return oldItem.VideoId === newItem.VideoId
        }

        override fun areContentsTheSame(oldItem: CatWiseVideoData, newItem: CatWiseVideoData): Boolean {
            return oldItem.VideoId == newItem.VideoId
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder {
        return DataViewHolder(SeeAllGridVideoBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        val video = getItem(position)
        holder.itemView.setOnClickListener {
            onclickListener.onclick(video)
        }
        holder.bind(video)
    }

    class OnclickListener(val clickListener: (video: CatWiseVideoData) -> Unit) {
        fun onclick(video: CatWiseVideoData) = clickListener(video)
    }
}
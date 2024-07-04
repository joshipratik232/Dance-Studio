package com.example.apptest.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.apptest.databinding.RecentViewItemBinding
import com.example.apptest.model.recentVideo.RecentVideoData

class RecentVideoAdapter(private val onclickListener: OnclickListener) : ListAdapter<RecentVideoData, RecentVideoAdapter.DataViewHolder>(DiffCallBack){
    class DataViewHolder(private val binding: RecentViewItemBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(recentVideoData : RecentVideoData){
            binding.data = recentVideoData
            binding.executePendingBindings()
        }
    }
    companion object DiffCallBack : DiffUtil.ItemCallback<RecentVideoData>() {
        override fun areItemsTheSame(oldItem: RecentVideoData, newItem: RecentVideoData): Boolean {
            return oldItem.VideoId === newItem.VideoId
        }

        override fun areContentsTheSame(oldItem: RecentVideoData, newItem: RecentVideoData): Boolean {
            return oldItem.VideoId == newItem.VideoId
        }

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder {
        return DataViewHolder(RecentViewItemBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        val video = getItem(position)
        holder.itemView.setOnClickListener {
            onclickListener.onclick(video)
        }
        holder.bind(video)
    }
    class OnclickListener(val clickListener: (video: RecentVideoData) -> Unit) {
        fun onclick(video: RecentVideoData) = clickListener(video)
    }
}
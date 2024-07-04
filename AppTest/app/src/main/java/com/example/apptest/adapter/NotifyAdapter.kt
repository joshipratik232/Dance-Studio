package com.example.apptest.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.apptest.databinding.NotifyListItemBinding
import com.example.apptest.model.notify.NotifyData

class NotifyAdapter : ListAdapter<NotifyData, NotifyAdapter.DataViewHolder>(DiffCallBack){
    class DataViewHolder(private val binding: NotifyListItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(notifyData: NotifyData?){
            binding.data = notifyData
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder {
        return DataViewHolder(NotifyListItemBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    companion object DiffCallBack: DiffUtil.ItemCallback<NotifyData>(){
        override fun areItemsTheSame(oldItem: NotifyData, newItem: NotifyData): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: NotifyData, newItem: NotifyData): Boolean {
            return oldItem.Id == newItem.Id
        }
    }
}
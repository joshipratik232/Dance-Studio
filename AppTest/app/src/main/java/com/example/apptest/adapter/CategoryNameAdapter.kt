package com.example.apptest.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.apptest.databinding.FragmentCategoryNameBinding
import com.example.apptest.fragment.*
import com.example.apptest.model.categoryHome.CategoryData
import com.example.apptest.model.categoryHome.Video
import com.example.apptest.viewModel.category.CtyViewModel
import timber.log.Timber


class CategoryNameAdapter(private val onclickListener: OnclickListener) : ListAdapter<CategoryData, CategoryNameAdapter.DataViewHolder>(DiffCallBack) {
    @Suppress("DEPRECATED_IDENTITY_EQUALS")
    companion object DiffCallBack : DiffUtil.ItemCallback<CategoryData>() {
        override fun areItemsTheSame(oldItem: CategoryData, newItem: CategoryData): Boolean {
            return oldItem.Id === newItem.Id
        }

        override fun areContentsTheSame(oldItem: CategoryData, newItem: CategoryData): Boolean {
            return oldItem.Id == newItem.Id
        }

        lateinit var video: Video
    }

    class DataViewHolder(private val binding: FragmentCategoryNameBinding) : RecyclerView.ViewHolder(binding.root) {


        val seeAll: TextView = binding.seeAll
        val recyclerView = binding.categoryVideo
        fun bind(categoryData: CategoryData?) {
            binding.name = categoryData
            binding.executePendingBindings()
        }
    }

    class OnclickListener(val clickListener: (id: Int) -> Unit) {
        fun onclick(id: Int) = clickListener(id)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder {
        return DataViewHolder(FragmentCategoryNameBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        val data = getItem(position)
        with(holder.itemView) {
            val adapter = CategoryVideoAdapter(CategoryVideoAdapter.OnclickListener {
                try {
                    /*val bundle = Bundle()
                    bundle.putParcelable("video", it)*/
                    /*(context as BaseActivity).supportFragmentManager.beginTransaction()
                            .replace(R.id.container, VideoFragment(), "OptionsFragment")
                            .addToBackStack(null).commit()*/
                    this.findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToVideoFragment(it, null, null))
                } catch (e: IllegalArgumentException) {
                    Timber.d("can't navigate : $e")
                }
            })
            holder.recyclerView.layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.HORIZONTAL, false)
            adapter.submitList(data.Videos)
            holder.recyclerView.adapter = adapter
        }

        holder.seeAll.setOnClickListener {
            onclickListener.onclick(data.Id)
        }

        holder.bind(data)
    }
}
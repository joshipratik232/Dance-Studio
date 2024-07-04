package com.example.apptest.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.apptest.databinding.VideoListRowBinding
import com.example.apptest.model.downloaded.VideoModel

class DownloadVideoAdapter(private val videoList: List<VideoModel>, private val onclickListener: OnclickListener?, private val deleteVideoFromList: DeleteVideo?)
    : ListAdapter<VideoModel, DownloadVideoAdapter.DataViewHolder?>(DiffCallBack) {

    class DataViewHolder(private val binding: VideoListRowBinding) : RecyclerView.ViewHolder(binding.root) {
        var deleteVideo = binding.deleteVideo
        fun bind(videoModel: VideoModel){
            binding.videoModel = videoModel
            binding.executePendingBindings()
        }
    }

    companion object DiffCallBack : DiffUtil.ItemCallback<VideoModel>() {
        override fun areItemsTheSame(oldItem: VideoModel, newItem: VideoModel): Boolean {
            return oldItem.videoName === newItem.videoName
        }

        override fun areContentsTheSame(oldItem: VideoModel, newItem: VideoModel): Boolean {
            return oldItem.videoName == newItem.videoName
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder {
        return DataViewHolder(VideoListRowBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        with(holder) {
            val mVideo = videoList.get(position)
            bind(mVideo)
            itemView.setOnClickListener {
                onclickListener!!.onclick(mVideo.filePath)
            }
            deleteVideo.setOnClickListener {
                deleteVideoFromList!!.onclick(mVideo.videoName, position)
            }
        }
    }

    class OnclickListener(val clickListener: (path: String?) -> Unit) {
        fun onclick(path: String?) = clickListener(path)
    }

    class DeleteVideo(val clickListener: (path: String?, position: Int?) -> Unit) {
        fun onclick(path: String?, position: Int?) = clickListener(path,position)
    }

    override fun getItemCount(): Int {
        return videoList.size
    }
}
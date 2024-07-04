package com.example.apptest.viewModel.video

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.apptest.model.categoryHome.Video
import com.example.apptest.model.categorySeeAll.CatWiseVideoData
import com.example.apptest.model.recentVideo.RecentVideoData

class VideoViewModelFactory(
        private val video: Video?,
        private val recentVideoData: RecentVideoData?,
        private val catWiseVideoData: CatWiseVideoData?,
        private val application: Application) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(VideoViewModel::class.java)) {
            return VideoViewModel(video, recentVideoData, catWiseVideoData, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
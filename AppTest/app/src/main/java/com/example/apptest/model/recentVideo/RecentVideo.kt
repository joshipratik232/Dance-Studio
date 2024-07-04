package com.example.apptest.model.recentVideo

data class RecentVideo(
        val Data: List<RecentVideoData>,
        val Message: String,
        val StatusCode: Int,
        val Success: Boolean
)
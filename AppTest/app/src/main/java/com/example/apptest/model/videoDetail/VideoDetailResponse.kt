package com.example.apptest.model.videoDetail

data class VideoDetailResponse(
        val Data: List<VideoDetailData>,
        val Message: String,
        val StatusCode: Int,
        val Success: Boolean
)
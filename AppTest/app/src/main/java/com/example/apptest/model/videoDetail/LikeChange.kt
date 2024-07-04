package com.example.apptest.model.videoDetail

data class LikeChange(
        val Data: List<LikeData>,
        val Message: String,
        val StatusCode: Int,
        val Success: Boolean
)
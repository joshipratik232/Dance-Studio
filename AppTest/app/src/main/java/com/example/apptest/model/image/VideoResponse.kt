package com.example.apptest.model.image

data class VideoResponse(
        val Data: List<ImageData>,
        val Message: String,
        val StatusCode: Int,
        val Success: Boolean
)
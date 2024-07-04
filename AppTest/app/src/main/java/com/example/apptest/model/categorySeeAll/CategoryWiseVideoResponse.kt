package com.example.apptest.model.categorySeeAll

data class CategoryWiseVideoResponse(
        val Data: List<CatWiseVideoData>,
        val Message: String,
        val StatusCode: Int,
        val Success: Boolean
)
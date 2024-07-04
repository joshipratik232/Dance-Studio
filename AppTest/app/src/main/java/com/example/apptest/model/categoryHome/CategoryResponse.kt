package com.example.apptest.model.categoryHome

data class CategoryResponse(
        val Data: List<CategoryData>,
        val Message: String,
        val StatusCode: Int,
        val Success: Boolean
)
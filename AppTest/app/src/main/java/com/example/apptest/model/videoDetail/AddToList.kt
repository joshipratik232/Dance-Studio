package com.example.apptest.model.videoDetail

data class AddToList(
        val Data: List<AddToListData>,
        val Message: String,
        val StatusCode: Int,
        val Success: Boolean)
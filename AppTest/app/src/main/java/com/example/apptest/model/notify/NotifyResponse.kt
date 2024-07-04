package com.example.apptest.model.notify

data class NotifyResponse(
        val Data: List<NotifyData>,
        val Message: String,
        val StatusCode: Int,
        val Success: Boolean
)
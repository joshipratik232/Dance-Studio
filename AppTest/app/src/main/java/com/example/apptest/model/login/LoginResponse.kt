package com.example.apptest.model.login

data class LoginResponse(
        val Data: List<LoginData>,
        val Message: String,
        val StatusCode: Int,
        val Success: Boolean
)
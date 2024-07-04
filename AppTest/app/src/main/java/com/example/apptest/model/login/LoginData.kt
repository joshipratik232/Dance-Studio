package com.example.apptest.model.login

data class LoginData(
        var EmailId: String,
        val Phone: String,
        val ResponseMessage: String,
        val Token: String,
        val UserName: String
)
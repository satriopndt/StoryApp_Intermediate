package com.example.storyapp.pref


data class AuthToken(
    var token: String,
    var userId: String,
    var name: String,
    var isLogin: Boolean
)

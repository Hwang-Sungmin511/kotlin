package com.example.firebase.data

import retrofit2.http.GET

// data class 와 서비스를 하는 api를 개별적으로 선언해야 하나?

data class Post(
    val id: Int,
    val title: String,
    val body: String
)

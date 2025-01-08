package com.example.firebase.service

import com.example.firebase.data.DataModel
import com.example.firebase.data.Post
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

// 대규모 프로젝트에서는 인터페이스를 분리하는 것이 권장
interface ApiService {
    @POST("/send")
    fun sendData(@Body data: DataModel): Call<Map<String, Any>>

    @GET("/get")
    fun getData(): Call<Map<String, Any>>

    @GET("posts")
    suspend fun getPosts(): List<Post>
}
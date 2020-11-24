package com.example.fakeahpeeclient.network

import com.example.fakeahpeeclient.network.model.Post
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*


interface PostNetwork {

    @GET("posts")
    fun getPosts(): Call<List<Post>>

    @DELETE("posts/{num}")
    fun deletePost(@Path("num") num: Int): Call<ResponseBody>

    @POST("posts")
    fun postPost(@Body post: Post): Call<Post>
}
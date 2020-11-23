package com.example.fakeahpeeclient.singleton

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.example.fakeahpeeclient.network.PostNetwork
import com.example.fakeahpeeclient.network.model.Post
import okhttp3.ResponseBody
import retrofit2.*
import retrofit2.converter.moshi.MoshiConverterFactory

class FakeAhPeeClient : Application() {

    private lateinit var retrofit: Retrofit
    var posts = mutableListOf<Post>()
    private lateinit var postNetwork: PostNetwork
    private lateinit var moshiConverterFactory: MoshiConverterFactory

    override fun onCreate() {
        super.onCreate()
        Log.i("YO", "Application was created")
        instance = this

        moshiConverterFactory = MoshiConverterFactory.create()
        retrofit = Retrofit
            .Builder()
            .baseUrl("https://jsonplaceholder.typicode.com/")
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
        postNetwork = retrofit.create()
    }

    fun loadPosts(success: (response: List<Post>) -> Unit, failure: (t: Throwable) -> Unit) {
        postNetwork.getPosts().enqueue(object : Callback<List<Post>> {
            override fun onResponse(call: Call<List<Post>>, response: Response<List<Post>>) {
                if (response.isSuccessful) {
                    success(response.body()!!)
                }
            }

            override fun onFailure(call: Call<List<Post>>, t: Throwable) {
                failure(t)
            }
        })
    }

    fun deletePost(id: Int) {
        postNetwork.deletePost(id).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@FakeAhPeeClient, "Success", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {

            }

        })
    }

    companion object {
        lateinit var instance: FakeAhPeeClient
    }
}
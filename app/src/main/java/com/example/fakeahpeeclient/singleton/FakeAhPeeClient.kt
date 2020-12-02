package com.example.fakeahpeeclient.singleton

import android.app.Application
import android.util.Log
import androidx.room.Room
import com.example.fakeahpeeclient.network.PostNetwork
import com.example.fakeahpeeclient.model.Post
import com.example.fakeahpeeclient.storage.AppDatabase
import com.example.fakeahpeeclient.storage.PostDAO
import com.example.fakeahpeeclient.ui.PostsAdapter
import okhttp3.ResponseBody
import retrofit2.*
import retrofit2.converter.moshi.MoshiConverterFactory

class FakeAhPeeClient : Application() {

    private lateinit var retrofit: Retrofit
    var postsAdapter: PostsAdapter? = null
    private lateinit var postNetwork: PostNetwork
    private lateinit var moshiConverterFactory: MoshiConverterFactory
    private lateinit var db: AppDatabase
    private lateinit var postDAO: PostDAO

    override fun onCreate() {
        super.onCreate()
        Log.i("YO", "Application was created")
        instance = this

        moshiConverterFactory = MoshiConverterFactory.create()
        retrofit = Retrofit
            .Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
        postNetwork = retrofit.create()

        db = Room
            .databaseBuilder(applicationContext, AppDatabase::class.java, "database").build()

        postDAO = db.postDAO()
    }

    fun fetchPosts(
        success: (response: List<Post>) -> Unit,
        failure: ((Throwable) -> Unit)
    ) {
        postNetwork.getPosts().enqueue(commonCallback<List<Post>>(success, failure))
    }

    fun loadPosts(){
        postDAO.getAll()
    }

    fun deletePost(
        id: Int,
        success: (response: ResponseBody) -> Unit,
        failure: (Throwable) -> Unit
    ) {
        postNetwork.deletePost(id).enqueue(commonCallback<ResponseBody>(success, failure))
    }

    fun postPost(
        post: Post,
        success: (post: Post) -> Unit,
        failure: ((Throwable) -> Unit)
    ) {
        postDAO.insert(post)
        postNetwork.postPost(post).enqueue(commonCallback<Post>(success, failure))
    }

    private fun <T> commonCallback(
        success: (response: T) -> Unit,
        failure: (Throwable) -> Unit
    ): Callback<T> {
        return object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                if (response.isSuccessful) {
                    success(response.body()!!)
                }
                call.cancel()
            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                failure(t)
                call.cancel()
            }
        }
    }

    companion object {

        lateinit var instance: FakeAhPeeClient
        const val BASE_URL = "https://jsonplaceholder.typicode.com/"
    }
}
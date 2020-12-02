package com.example.fakeahpeeclient.singleton

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.room.Room
import com.example.fakeahpeeclient.network.PostNetwork
import com.example.fakeahpeeclient.model.Post
import com.example.fakeahpeeclient.storage.AppDatabase
import com.example.fakeahpeeclient.storage.PostDAO
import com.example.fakeahpeeclient.ui.PostsAdapter
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
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
    var sharedPref: SharedPreferences? = null
    var isBDEmpty = true

    override fun onCreate() {
        super.onCreate()
        Log.i("YO", "Application was created")
        instance = this

        sharedPref = getSharedPreferences(BD_IS_EMPTY, Context.MODE_PRIVATE)
        isBDEmpty = sharedPref?.getBoolean(IS_BD_EMPTY, true) ?: true

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

    fun loadPosts() {
        GlobalScope.launch {
            val result = postDAO.getAll()
            postsAdapter?.data?.addAll(result)
        }
    }

    fun deletePost(
        id: Int,
        success: (response: ResponseBody) -> Unit,
        failure: (Throwable) -> Unit
    ) {
        postNetwork.deletePost(id).enqueue(commonCallback<ResponseBody>(success, failure))
    }

    fun deletePostFromBD(post: Post){
        GlobalScope.launch {
            postDAO.deletePost(post)
        }
    }

    fun clearBD(){
        GlobalScope.launch {
            postDAO.deleteAll()
        }
    }

    fun postPost(
        post: Post,
        success: (post: Post) -> Unit,
        failure: ((Throwable) -> Unit)
    ) {
        GlobalScope.launch { postDAO.insertAll(post) }
        postNetwork.postPost(post).enqueue(commonCallback<Post>(success, failure))
    }

    fun persistAllPosts(posts: List<Post>) {
        GlobalScope.launch { postDAO.insertAll(*posts.toTypedArray()) }
    }

    fun loadAllPosts(){
        GlobalScope.launch {
            postsAdapter?.apply {
                data.addAll(postDAO.getAll())
                notifyDataSetChanged()
            }
        }
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
        const val BD_IS_EMPTY = "BD_IS_EMPTY"
        const val IS_BD_EMPTY = "110101010101023"
    }
}
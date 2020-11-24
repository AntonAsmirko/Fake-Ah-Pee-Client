package com.example.fakeahpeeclient.singleton

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import com.example.fakeahpeeclient.network.PostNetwork
import com.example.fakeahpeeclient.network.model.Post
import okhttp3.ResponseBody
import retrofit2.*
import retrofit2.converter.moshi.MoshiConverterFactory

class FakeAhPeeClient : Application() {

    private lateinit var retrofit: Retrofit
    var posts = MutableLiveData<MutableList<Post>>().apply { value = mutableListOf() }
    private lateinit var postNetwork: PostNetwork
    private lateinit var moshiConverterFactory: MoshiConverterFactory
    val commonErrorNotifierAction: (() -> Unit) -> ((Throwable) -> Unit) =
        { fn ->
            { it ->
                Toast.makeText(
                    this@FakeAhPeeClient,
                    it.localizedMessage,
                    Toast.LENGTH_LONG
                ).show()
                fn()
            }
        }

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
    }

    fun loadPosts(
        success: (response: List<Post>) -> Unit,
        failure: ((Throwable) -> Unit)
    ) {
        postNetwork.getPosts().enqueue(commonCallback<List<Post>>(success, failure))
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
            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                failure(t)
            }
        }
    }

    companion object {

        fun <T> commonPrevRunner(
            fn1: () -> Unit,
            fn2: (() -> Unit, ((T) -> Unit)?) -> ((T) -> Unit)
        ): (() -> Unit, ((T) -> Unit)?) -> ((T) -> Unit) {
            return { f1, f2 -> fn1(); fn2(f1, f2) }
        }

        lateinit var instance: FakeAhPeeClient
        const val BASE_URL = "https://jsonplaceholder.typicode.com/"
    }
}
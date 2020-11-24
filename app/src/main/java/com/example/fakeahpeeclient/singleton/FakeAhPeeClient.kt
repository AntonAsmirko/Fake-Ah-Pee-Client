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
    private val commonErrorNotifier: (Throwable) -> Unit = {
        Toast.makeText(
            this@FakeAhPeeClient,
            it.localizedMessage,
            Toast.LENGTH_LONG
        ).show()
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

    fun loadPosts(success: (response: List<Post>) -> Unit, failure: (t: Throwable) -> Unit) {
        postNetwork.getPosts().enqueue(commonCallback<List<Post>>(success, failure))
    }

    fun deletePost(id: Int) {
        postNetwork.deletePost(id).enqueue(commonCallback<ResponseBody>({
            Toast.makeText(
                this@FakeAhPeeClient,
                "post was successfully deleted",
                Toast.LENGTH_LONG
            ).show()
            Log.i("DELETE", "post was successfully deleted")
        }, commonErrorNotifier))
    }

    fun postPost(post: Post) {
        postNetwork.postPost(post).enqueue(
            commonCallback<Post>(
                {
                    Toast.makeText(
                        this@FakeAhPeeClient,
                        "post $it was successfully published",
                        Toast.LENGTH_LONG
                    ).show()
                    Log.i("POST", "post $it was successfully published")
                },
                commonErrorNotifier
            )
        )
    }

    private fun <T> commonCallback(
        success: (response: T) -> Unit,
        failure: (t: Throwable) -> Unit
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
        lateinit var instance: FakeAhPeeClient
        const val BASE_URL = "https://jsonplaceholder.typicode.com/"
    }
}
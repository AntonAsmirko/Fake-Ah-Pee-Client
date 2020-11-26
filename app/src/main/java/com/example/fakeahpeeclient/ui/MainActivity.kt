package com.example.fakeahpeeclient.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.fakeahpeeclient.R
import com.example.fakeahpeeclient.network.PostNetwork
import com.example.fakeahpeeclient.network.model.Post
import com.example.fakeahpeeclient.singleton.FakeAhPeeClient
import com.squareup.moshi.Moshi
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.post_holder.*
import retrofit2.*
import retrofit2.converter.moshi.MoshiConverterFactory

class MainActivity : AppCompatActivity() {

    companion object {
        const val MAKE_POST_REQUEST = 101
    }

    private var postsAdapter: PostsAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        initRecycler(FakeAhPeeClient.instance.posts)
        Log.i("YO", "Activity was created")
        toolbar.setOnMenuItemClickListener {
            val i = Intent(this, CreatePostActivity::class.java)
            startActivityForResult(i, MAKE_POST_REQUEST)
            postsAdapter?.notifyDataSetChanged()
            return@setOnMenuItemClickListener true
        }
        swipe_refresh_layout.setOnRefreshListener {
            postsAdapter?.clear()
            loadPosts(swipe_refresh_layout)
        }
        if (FakeAhPeeClient.instance.posts.isEmpty()) {
            progress_bar.visibility = View.VISIBLE
            loadPosts()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        postsAdapter?.clearResources()
        postsAdapter = null
    }

    private fun loadPosts(swipeRefreshLayout: SwipeRefreshLayout? = null) {
        FakeAhPeeClient.instance.loadPosts(
            {
                postsAdapter!!.data.addAll(it)
                progress_bar.visibility = View.GONE
                postsAdapter!!.notifyDataSetChanged()
            },
            {
                if (swipeRefreshLayout != null) swipeRefreshLayout.isRefreshing = false
                progress_bar.visibility = View.GONE
            })
    }

    private fun initRecycler(data: MutableList<Post>) {
        postsAdapter = PostsAdapter(data, progress_bar)
        recycler_posts.adapter = postsAdapter
        recycler_posts.layoutManager = LinearLayoutManager(this)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }
}

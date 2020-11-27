package com.example.fakeahpeeclient.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.fakeahpeeclient.R
import com.example.fakeahpeeclient.singleton.FakeAhPeeClient
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    companion object {
        const val MAKE_POST_REQUEST = 101
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        initRecycler()
        Log.i("YO", "Activity was created")
        toolbar.setOnMenuItemClickListener {
            val i = Intent(this, CreatePostActivity::class.java)
            startActivityForResult(i, MAKE_POST_REQUEST)
            FakeAhPeeClient.instance.postsAdapter?.notifyDataSetChanged()
            return@setOnMenuItemClickListener true
        }
        swipe_refresh_layout.setOnRefreshListener {
            FakeAhPeeClient.instance.postsAdapter?.clear()
            loadPosts(swipe_refresh_layout)
        }
        if (FakeAhPeeClient.instance.postsAdapter?.data?.isEmpty() ?: false) {
            progress_bar.visibility = View.VISIBLE
            loadPosts()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        FakeAhPeeClient.instance.postsAdapter?.clearResources()
    }

    private fun loadPosts(swipeRefreshLayout: SwipeRefreshLayout? = null) {
        FakeAhPeeClient.instance.loadPosts(
            {
                FakeAhPeeClient.instance.postsAdapter?.data?.addAll(it)
                progress_bar.visibility = View.GONE
                FakeAhPeeClient.instance.postsAdapter?.notifyDataSetChanged()
                if (swipeRefreshLayout != null) swipeRefreshLayout.isRefreshing = false
            },
            {
                if (swipeRefreshLayout != null) swipeRefreshLayout.isRefreshing = false
                progress_bar.visibility = View.GONE
            })
    }

    private fun initRecycler() {
        if (FakeAhPeeClient.instance.postsAdapter == null)
            FakeAhPeeClient.instance.postsAdapter = PostsAdapter(mutableListOf(), progress_bar)
        recycler_posts.adapter = FakeAhPeeClient.instance.postsAdapter
        recycler_posts.layoutManager = LinearLayoutManager(this)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }
}

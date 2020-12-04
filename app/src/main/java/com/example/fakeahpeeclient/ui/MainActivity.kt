package com.example.fakeahpeeclient.ui

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import androidx.appcompat.widget.Toolbar
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
            FakeAhPeeClient.instance.clearBD()
            fetchPosts(swipe_refresh_layout)
        }
        if (FakeAhPeeClient.instance.postsAdapter?.data?.isEmpty() ?: false) {
            if (FakeAhPeeClient.instance.isBDEmpty) {
                fetchPosts()
                FakeAhPeeClient.instance.isBDEmpty = false
                FakeAhPeeClient.instance.sharedPref?.edit()?.apply {
                    putBoolean(FakeAhPeeClient.IS_BD_EMPTY, false)
                    apply()
                }
            } else FakeAhPeeClient.instance.loadAllPosts()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        swipe_refresh_layout.isRefreshing = false
        FakeAhPeeClient.instance.postsAdapter?.clearResources()
    }

    private fun fetchPosts(swipeRefreshLayout: SwipeRefreshLayout? = null) {
        FakeAhPeeClient.instance.fetchPosts(
            {
                FakeAhPeeClient.instance.postsAdapter?.data?.addAll(it)
                FakeAhPeeClient.instance.postsAdapter?.notifyDataSetChanged()
                if (swipeRefreshLayout != null) swipeRefreshLayout.isRefreshing = false
                FakeAhPeeClient.instance.persistAllPosts(it)
            },
            {
                if (swipeRefreshLayout != null) swipeRefreshLayout.isRefreshing = false
            })
    }

    private fun initRecycler() {
        if (FakeAhPeeClient.instance.postsAdapter == null)
            FakeAhPeeClient.instance.postsAdapter = PostsAdapter(mutableListOf())
        recycler_posts.adapter = FakeAhPeeClient.instance.postsAdapter
        recycler_posts.layoutManager = LinearLayoutManager(this)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }
}

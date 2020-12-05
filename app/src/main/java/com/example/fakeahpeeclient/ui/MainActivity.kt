package com.example.fakeahpeeclient.ui

import android.content.Intent
import android.graphics.Canvas
import android.os.Bundle
import android.util.Log
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
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
        val swipeController = SwipeController(object : SwipeControllerActions() {
            override fun onRightClicked(position: Int) {
                FakeAhPeeClient.instance.postsAdapter?.data?.removeAt(position)
                FakeAhPeeClient.instance.postsAdapter?.notifyItemRemoved(position)
                FakeAhPeeClient.instance.postsAdapter?.notifyItemRangeChanged(
                    position,
                    FakeAhPeeClient.instance.postsAdapter?.data?.size ?: 0
                )
            }
        })
        recycler_posts.addItemDecoration(object : ItemDecoration() {
            override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
                swipeController.onDraw(c)
            }
        })
        val itemTouchHelper = ItemTouchHelper(swipeController).apply {
            this.attachToRecyclerView(recycler_posts)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }
}

package com.example.fakeahpeeclient.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
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

    private lateinit var postsAdapter: PostsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        initRecycler(FakeAhPeeClient.instance.posts)
        Log.i("YO", "Activity was created")
        toolbar.setOnMenuItemClickListener {
            val i = Intent(this, CreatePostActivity::class.java)
            startActivity(i)
            return@setOnMenuItemClickListener true
        }
        if (FakeAhPeeClient.instance.posts.size == 0) {
            FakeAhPeeClient.instance.loadPosts({
                postsAdapter.data.addAll(it)
                postsAdapter.notifyDataSetChanged()
            },
                {
                    Toast.makeText(this@MainActivity, "LOX", Toast.LENGTH_SHORT).show()
                })
        }
    }

    private fun initRecycler(data: MutableList<Post>) {
        postsAdapter = PostsAdapter(data)
        recycler_posts.adapter = postsAdapter
        recycler_posts.layoutManager = LinearLayoutManager(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        FakeAhPeeClient.instance.posts = postsAdapter.data
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }
}

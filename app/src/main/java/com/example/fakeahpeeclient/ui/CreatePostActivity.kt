package com.example.fakeahpeeclient.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.fakeahpeeclient.R
import com.example.fakeahpeeclient.network.model.Post
import com.example.fakeahpeeclient.singleton.FakeAhPeeClient
import kotlinx.android.synthetic.main.activity_create_post.*

class CreatePostActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_post)
        publish_button.setOnClickListener {
            val title = edit_title.text.toString()
            val content = edit_content.text.toString()
            var maxId = -1
            FakeAhPeeClient.instance.posts.value!!.forEach { if (it.id > maxId) maxId = it.id }
            Post(content, ++maxId, title, 1).apply {
                FakeAhPeeClient.instance.postPost(this)
                FakeAhPeeClient.instance.posts.value!!.add(this)
                FakeAhPeeClient.instance.posts.value = FakeAhPeeClient.instance.posts.value
                finish()
            }
        }
    }
}
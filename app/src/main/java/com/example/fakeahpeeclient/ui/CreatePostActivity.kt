package com.example.fakeahpeeclient.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.fakeahpeeclient.R
import com.example.fakeahpeeclient.model.Post
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
            FakeAhPeeClient.instance.postsAdapter?.data?.forEach { if (it.id > maxId) maxId = it.id }
            Post(content, ++maxId, title, 1).apply {
                FakeAhPeeClient.instance.postPost(
                    this,
                    {
                        Toast.makeText(
                            this@CreatePostActivity,
                            "post $it was successfully published",
                            Toast.LENGTH_LONG
                        ).show()
                        Log.i("POST", "post $it was successfully published")
                    },
                    {
                        Toast.makeText(
                            this@CreatePostActivity,
                            "Some error occured while post request",
                            Toast.LENGTH_LONG
                        ).show()
                    })
                FakeAhPeeClient.instance.postsAdapter?.data?.add(this)
                FakeAhPeeClient.instance.postsAdapter?.notifyDataSetChanged()
                finish()
            }
        }
    }
}
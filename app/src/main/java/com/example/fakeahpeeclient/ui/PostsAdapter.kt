package com.example.fakeahpeeclient.ui

import android.app.Notification
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.fakeahpeeclient.R
import com.example.fakeahpeeclient.network.model.Post
import com.example.fakeahpeeclient.singleton.FakeAhPeeClient
import kotlinx.android.synthetic.main.post_holder.view.*
import okhttp3.ResponseBody

class PostsAdapter(
    var data: MutableList<Post>,
    val callbackProgressBar: () -> Unit,
    val callbackNotification: (ResponseBody) -> Unit,
    val callbackTurnOnProgressBar: () -> Unit
) :
    RecyclerView.Adapter<PostsAdapter.PostHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostHolder {
        return PostHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.post_holder, parent, false)
        )
    }

    override fun onBindViewHolder(holder: PostHolder, position: Int) {
        val post = data[position]
        holder.fillView(post.title, post.body, post.id, post.userId)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class PostHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        lateinit var title: String
        lateinit var content: String
        var id = -1
        var userId = -1

        fun fillView(title: String, content: String, id: Int, userId: Int) {
            this.title = title
            this.content = content
            this.id = id
            this.userId = userId
            view.title.text = title.trim()
            view.content.text = content.trim()
            view.delete_button.setOnClickListener {
                FakeAhPeeClient.instance.deletePost(
                    this.id,
                    FakeAhPeeClient.commonPrevRunner<ResponseBody>(
                        callbackTurnOnProgressBar,
                        { fn1, fn2 -> { it -> fn1(); if (fn2 != null) fn2(it) } }
                    )(
                        callbackProgressBar,
                        callbackNotification
                    ),
                    FakeAhPeeClient.instance.commonErrorNotifierAction(callbackProgressBar)
                )
                var i = -1
                data.forEachIndexed { index, post ->
                    if (post.id == this.id) {
                        i = index
                        return@forEachIndexed
                    }
                }
                data.removeAt(i)
                this@PostsAdapter.notifyDataSetChanged()
            }
        }
    }

}
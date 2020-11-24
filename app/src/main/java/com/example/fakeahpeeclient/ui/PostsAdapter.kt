package com.example.fakeahpeeclient.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.fakeahpeeclient.R
import com.example.fakeahpeeclient.network.model.Post
import com.example.fakeahpeeclient.singleton.FakeAhPeeClient
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.post_holder.view.*

class PostsAdapter(var data: MutableList<Post>) : RecyclerView.Adapter<PostsAdapter.PostHolder>() {

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

    inner class PostHolder(val view: View) : RecyclerView.ViewHolder(view) {
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
                FakeAhPeeClient.instance.deletePost(this.id)
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
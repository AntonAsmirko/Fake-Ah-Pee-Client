package com.example.fakeahpeeclient.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ProgressBar
import androidx.recyclerview.widget.RecyclerView
import com.example.fakeahpeeclient.R
import com.example.fakeahpeeclient.model.Post
import com.example.fakeahpeeclient.singleton.FakeAhPeeClient
import kotlinx.android.synthetic.main.post_holder.view.*

class PostsAdapter(
    var data: MutableList<Post>,
    var progressBar: ProgressBar?,
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
        holder.view.animation =
            AnimationUtils.loadAnimation(holder.itemView.context, R.anim.anim_recycler_item)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun clear() {
        data.clear()
        notifyDataSetChanged()
    }

    fun clearResources(){
        progressBar = null
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
                progressBar?.visibility = View.VISIBLE
                FakeAhPeeClient.instance.deletePost(
                    this.id,
                    {
                        progressBar?.visibility = View.GONE
                    },
                    {
                        progressBar?.visibility = View.GONE
                    }
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
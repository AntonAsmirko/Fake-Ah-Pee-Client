package com.example.fakeahpeeclient.ui

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fakeahpeeclient.R
import com.example.fakeahpeeclient.model.Post
import kotlinx.android.synthetic.main.post_holder.view.*

class PostsAdapter(
    var data: MutableList<Post>,
    val context: Context,
    private val onItemClickListener: OnItemClickListener, ):
    RecyclerView.Adapter<PostsAdapter.PostHolder>() {

    private lateinit var rootView: View
    private lateinit var layoutManager: LinearLayoutManager

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostHolder {
        rootView = parent
        return PostHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.post_holder, parent, false),
            onItemClickListener
        )
    }

    override fun onBindViewHolder(holder: PostHolder, position: Int) {
        holder.fillView(data[position])
        holder.view.animation =
            AnimationUtils.loadAnimation(holder.itemView.context, R.anim.anim_recycler_item)
    }

    override fun getItemCount(): Int = data.size

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        layoutManager = recyclerView.layoutManager as LinearLayoutManager
    }

    inner class PostHolder(val view: View, itemClickListener: OnItemClickListener) :
        RecyclerView.ViewHolder(view) {
        lateinit var title: String
        lateinit var content: String
        var id = -1
        var userId = -1
        lateinit var post: Post

        init {

            view.setOnClickListener {
                itemClickListener.onItemClick(view, layoutPosition, post)
            }
        }

        @SuppressLint("ClickableViewAccessibility")
        fun fillView(post: Post) {
            this.title = post.title
            this.content = post.body
            this.id = post.id
            this.userId = post.userId
            this.post = post
            view.title.text = title.trim()
            view.content.text = content.trim()
//            view.delete_button.setOnTouchListener { v, event ->
//                if (event.action == MotionEvent.ACTION_UP && view.post_holder.currentState == R.id.right_card_off_screen) {
//                    CoroutineScope(Dispatchers.Main).launch {
//                        FakeAhPeeClient.instance.deletePost(
//                            id,
//                            {
//                            },
//                            {
//                            }
//                        )
//                    }
//                    var i = -1
//                    data.forEachIndexed { index, post ->
//                        if (post.id == id) {
//                            i = index
//                            CoroutineScope(Dispatchers.Main).launch {
//                                FakeAhPeeClient.instance.deletePostFromBD(post)
//                            }
//                            return@forEachIndexed
//                        }
//                    }
//                    view.post_holder.transitionToState(R.id.start)
//                    data.removeAt(i)
//                    this@PostsAdapter.notifyItemRemoved(i)
//                    this@PostsAdapter.notifyItemRangeChanged(i, data.size)
//                }
//                return@setOnTouchListener false
//            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(
            view: View,
            position: Int,
            data: Post
        )
    }
}
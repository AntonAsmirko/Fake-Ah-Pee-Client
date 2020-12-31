package com.example.fakeahpeeclient.ui

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationSet
import android.view.animation.AnimationUtils
import android.widget.ProgressBar
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.marginTop
import androidx.recyclerview.widget.RecyclerView
import androidx.vectordrawable.graphics.drawable.Animatable2Compat
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import com.example.fakeahpeeclient.R
import com.example.fakeahpeeclient.model.Post
import com.example.fakeahpeeclient.singleton.FakeAhPeeClient
import kotlinx.android.synthetic.main.post_holder.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PostsAdapter(
    var data: MutableList<Post>,
    val context: Context
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

    fun clearResources() {
    }

    inner class PostHolder(val view: View) : RecyclerView.ViewHolder(view) {
        lateinit var title: String
        lateinit var content: String
        var id = -1
        var userId = -1

        init {
            val animationBottomForward =
                AnimatedVectorDrawableCompat.create(context, R.drawable.bottom_right_liquid_forward)
            val animationBottomReverse =
                AnimatedVectorDrawableCompat.create(context, R.drawable.bottom_right_liquid_reverce)
            animationBottomReverse!!.registerAnimationCallback(object :
                Animatable2Compat.AnimationCallback() {
                override fun onAnimationEnd(drawable: Drawable) {
                    animationBottomForward!!.start()
                    view.bottom_liquid.setImageDrawable(animationBottomForward)
                }
            })
            animationBottomForward!!.registerAnimationCallback(object :
                Animatable2Compat.AnimationCallback() {
                override fun onAnimationEnd(drawable: Drawable) {
                    animationBottomReverse.start()
                    view.bottom_liquid.setImageDrawable(animationBottomReverse)
                }
            })
            animationBottomForward.start()
            view.bottom_liquid.setImageDrawable(animationBottomForward)
        }

        @SuppressLint("ClickableViewAccessibility")
        fun fillView(title: String, content: String, id: Int, userId: Int) {
            this.title = title
            this.content = content
            this.id = id
            this.userId = userId
            view.title.text = title.trim()
            view.content.text = content.trim()
            view.delete_button.setOnTouchListener { v, event ->
                if (event.action == MotionEvent.ACTION_UP && view.motion_post_holder.currentState == R.id.right_card_off_screen) {
                    CoroutineScope(Dispatchers.Main).launch {
                        FakeAhPeeClient.instance.deletePost(
                            id,
                            {
                            },
                            {
                            }
                        )
                    }
                    var i = -1
                    data.forEachIndexed { index, post ->
                        if (post.id == id) {
                            i = index
                            CoroutineScope(Dispatchers.Main).launch {
                                FakeAhPeeClient.instance.deletePostFromBD(post)
                            }
                            return@forEachIndexed
                        }
                    }
                    view.motion_post_holder.transitionToState(R.id.start)
                    data.removeAt(i)
                    this@PostsAdapter.notifyDataSetChanged()
                }
                return@setOnTouchListener false
            }
        }
    }

}
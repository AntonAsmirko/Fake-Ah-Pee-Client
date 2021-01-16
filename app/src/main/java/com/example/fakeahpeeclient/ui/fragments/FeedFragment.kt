package com.example.fakeahpeeclient.ui.fragments

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.eightbitlab.supportrenderscriptblur.SupportRenderScriptBlur
import com.example.fakeahpeeclient.R
import com.example.fakeahpeeclient.extensions.isClicked
import com.example.fakeahpeeclient.extensions.setTransitionListener
import com.example.fakeahpeeclient.model.Post
import com.example.fakeahpeeclient.singleton.FakeAhPeeClient
import com.example.fakeahpeeclient.ui.OnSwipeTouchListener
import com.example.fakeahpeeclient.ui.PostsAdapter
import kotlinx.android.synthetic.main.fragment_feed.*
import kotlinx.android.synthetic.main.fragment_feed.motion
import kotlinx.android.synthetic.main.fragment_feed.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FeedFragment : Fragment(), PostsAdapter.OnItemClickListener {

    companion object {
        const val TAG = "MotionRecyclerView"
        const val NO_CLIP = 0
        const val CLIP_TOP = 1
        const val CLIP_BOTTOM = 2
        var GLOBAL_COUNT = 0
    }

    private fun fetchPosts() =
        CoroutineScope(Dispatchers.Main).launch {
            FakeAhPeeClient.instance.fetchPosts(
                {
                    FakeAhPeeClient.instance.postsAdapter?.data?.addAll(it)
                    FakeAhPeeClient.instance.postsAdapter?.notifyDataSetChanged()
                    CoroutineScope(Dispatchers.IO).launch {
                        FakeAhPeeClient.instance.persistAllPosts(it)
                    }
                },
                {})
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_feed, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecycler()
        blur.setupWith(motion)
            .setBlurEnabled(false)
            .setBlurAutoUpdate(true)
            .setBlurAlgorithm(SupportRenderScriptBlur(activity as Context))
            .setBlurRadius(25f)
            .setOverlayColor(ResourcesCompat.getColor(resources, R.color.colorOverlay, null))
        if (FakeAhPeeClient.instance.postsAdapter?.data?.isEmpty() ?: false) {
            if (FakeAhPeeClient.instance.isBDEmpty) {
                fetchPosts()
                FakeAhPeeClient.instance.isBDEmpty = false
                FakeAhPeeClient.instance.sharedPref?.edit()?.apply {
                    putBoolean(FakeAhPeeClient.IS_BD_EMPTY, false)
                    apply()
                }
            } else CoroutineScope(Dispatchers.Main).launch {
                FakeAhPeeClient.instance.loadAllPosts() }
        }
        motion.callWhileIntercepting.add {
            if (like_button.isClicked(it)) motion.transitionToState(R.id.like_button_main_trash_down)
            false
        }
        motion.callWhileIntercepting.add {
            if (delete_button.isClicked(it)) motion.transitionToState(R.id.right_card_visible)
            false
        }
        motion.callWhileIntercepting.add {
            if (archive_button.isClicked(it)) motion.transitionToState(R.id.archive_button_visible)
            false
        }
        motion.callWhileIntercepting.add {
            if (!post_holder.isClicked(it) && !like_button.isClicked(it) && !delete_button.isClicked(
                    it
                ) && !archive_button.isClicked(it) && motion.currentState != R.id.start
            ) {
                when (motion.currentState) {
                    R.id.end -> {
                        motion.apply {
                            setTransition(R.id.end, R.id.start)
                            transitionToEnd()
                        }
                        blur.setBlurEnabled(false)
                    }
                }
                true
            } else
                false
        }
    }

    private fun initRecycler() {
        if (FakeAhPeeClient.instance.postsAdapter == null)
            FakeAhPeeClient.instance.postsAdapter =
                PostsAdapter(mutableListOf(), activity as Context, this)
        recycler_posts.layoutManager = LinearLayoutManager(activity as Context)
        recycler_posts.adapter = FakeAhPeeClient.instance.postsAdapter
    }

    override fun onItemClick(view: View, position: Int, data: Post) {
        val viewGroup = view as? ViewGroup
        val postCard = viewGroup?.run { findViewById<CardView>(R.id.post_card) } ?: return
        motion.post_holder.title.text = data.title
        motion.post_holder.content.text = data.body

        val rect = Rect()
        postCard.getLocalVisibleRect(rect)
        val clipType = when {
            rect.height() == postCard.height -> NO_CLIP
            rect.top > 0 -> CLIP_TOP
            else -> CLIP_BOTTOM
        }

        Log.d(TAG, "clip type: $clipType")
        motion.offsetDescendantRectToMyCoords(postCard, rect)
        Log.d(TAG, "localVisibleRect in parent coord system: $rect")

        val setStart = motion.getConstraintSet(R.id.start)
        setStart.clear(R.id.post_holder)
        setStart.constrainWidth(R.id.post_holder, postCard.width)
        setStart.constrainHeight(R.id.post_holder, postCard.height)
        setStart.connect(
            R.id.post_holder,
            ConstraintSet.START,
            ConstraintSet.PARENT_ID,
            ConstraintSet.START,
            rect.left
        )

        when (clipType) {
            CLIP_TOP -> {
                setStart.connect(
                    R.id.post_holder,
                    ConstraintSet.BOTTOM,
                    ConstraintSet.PARENT_ID,
                    ConstraintSet.BOTTOM,
                    motion.bottom - rect.bottom
                )
            }
            else -> {
                setStart.connect(
                    R.id.post_holder,
                    ConstraintSet.TOP,
                    ConstraintSet.PARENT_ID,
                    ConstraintSet.TOP,
                    rect.top
                )
            }
        }
        blur.setBlurEnabled(true)
        postCard.alpha = 0.0f
        motion.post_holder.visibility = View.VISIBLE
        motion.apply {
            updateState(R.id.start, setStart)
            swipeHandler = OnSwipeTouchListener(activity as Context,
                onSwipeDown = {
                    if (motion.currentState == R.id.end && motion.progress < 30f) {
                        motion.setTransition(R.id.end, R.id.top_card_expanded)
                        motion.progress = 0f
                    }
                },
                onSwipeLeft = {
                    if (motion.currentState == R.id.end && motion.progress < 30f) {
                        motion.setTransition(R.id.end, R.id.right_card_visible)
                        motion.progress = 0f
                    }
                })
            setTransition(R.id.start, R.id.end)
            setTransitionListener({ p1, _ ->
                motion.interceptChildren = true
                if (p1 == startState) {
                    postCard.alpha = 0.0f
                    motion.post_holder.alpha = 1.0f
                }
            },
                { p1 ->
                    if (p1 == R.id.start) {
                        motion.interceptChildren = false
                        postCard.alpha = 1.0f
                        motion.post_holder.alpha = 0.0f
                    }
                })
            transitionToEnd()
        }
    }
}
package com.example.fakeahpeeclient.ui

import android.content.Intent
import android.graphics.Rect
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.fakeahpeeclient.R
import com.example.fakeahpeeclient.model.Post
import com.example.fakeahpeeclient.singleton.FakeAhPeeClient
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.recycler_post_card.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), PostsAdapter.OnItemClickListener {

    companion object {
        const val MAKE_POST_REQUEST = 101
        const val TAG = "MotionRecyclerView"
        const val NO_CLIP = 0;
        const val CLIP_TOP = 1;
        const val CLIP_BOTTOM = 2;
    }

    private val itemTouchInterceptor = ItemTouchInterceptor()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        initRecycler()
        Log.i("YO", "Activity was created")
        toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.add_post -> {
                    val i = Intent(this, CreatePostActivity::class.java)
                    startActivityForResult(i, MAKE_POST_REQUEST)
                    FakeAhPeeClient.instance.postsAdapter?.notifyDataSetChanged()
                }
                R.id.switch_night_mode -> {
                    AppCompatDelegate.setDefaultNightMode(
                        if (FakeAhPeeClient.instance.isCurLightTheme)
                            AppCompatDelegate.MODE_NIGHT_YES
                        else AppCompatDelegate.MODE_NIGHT_NO
                    )
                    FakeAhPeeClient.instance.isCurLightTheme =
                        !FakeAhPeeClient.instance.isCurLightTheme
                }
            }
            return@setOnMenuItemClickListener true
        }
//        swipe_refresh_layout.setOnRefreshListener {
//            FakeAhPeeClient.instance.postsAdapter?.clear()
//            CoroutineScope(Dispatchers.Main).launch { FakeAhPeeClient.instance.clearBD() }
//            fetchPosts(swipe_refresh_layout)
//        }
        if (FakeAhPeeClient.instance.postsAdapter?.data?.isEmpty() ?: false) {
            if (FakeAhPeeClient.instance.isBDEmpty) {
                fetchPosts()
                FakeAhPeeClient.instance.isBDEmpty = false
                FakeAhPeeClient.instance.sharedPref?.edit()?.apply {
                    putBoolean(FakeAhPeeClient.IS_BD_EMPTY, false)
                    apply()
                }
            } else CoroutineScope(Dispatchers.Main).launch { FakeAhPeeClient.instance.loadAllPosts() }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        //swipe_refresh_layout.isRefreshing = false
    }

    private fun fetchPosts(swipeRefreshLayout: SwipeRefreshLayout? = null) =
        CoroutineScope(Dispatchers.Main).launch {
            FakeAhPeeClient.instance.fetchPosts(
                {
                    FakeAhPeeClient.instance.postsAdapter?.data?.addAll(it)
                    FakeAhPeeClient.instance.postsAdapter?.notifyDataSetChanged()
                    //if (swipeRefreshLayout != null) swipeRefreshLayout.isRefreshing = false
                    CoroutineScope(Dispatchers.IO).launch {
                        FakeAhPeeClient.instance.persistAllPosts(it)
                    }
                },
                {
                    //if (swipeRefreshLayout != null) swipeRefreshLayout.isRefreshing = false
                })
        }

    private fun initRecycler() {
        if (FakeAhPeeClient.instance.postsAdapter == null)
            FakeAhPeeClient.instance.postsAdapter = PostsAdapter(mutableListOf(), this, this)
        recycler_posts.adapter = FakeAhPeeClient.instance.postsAdapter
        recycler_posts.layoutManager = LinearLayoutManager(this)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onItemClick(view: View, position: Int, data: Post) {
        val viewGroup = view as? ViewGroup
        val postCard = viewGroup?.run { findViewById<AppCompatImageView>(R.id.post_card) } ?: return

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

        val set = motion.getConstraintSet(R.id.start)
        set.clear(R.id.post_holder)
        set.constrainWidth(R.id.post_holder, postCard.width)
        set.constrainHeight(R.id.post_holder, postCard.height)
        set.connect(
            R.id.post_holder,
            ConstraintSet.START,
            ConstraintSet.PARENT_ID,
            ConstraintSet.START,
            rect.left
        )
        when (clipType) {
            CLIP_TOP -> set.connect(
                R.id.post_holder,
                ConstraintSet.BOTTOM,
                ConstraintSet.PARENT_ID,
                ConstraintSet.BOTTOM,
                motion.bottom - rect.bottom
            )
            else -> set.connect(
                R.id.post_holder,
                ConstraintSet.TOP,
                ConstraintSet.PARENT_ID,
                ConstraintSet.TOP,
                rect.top
            )
        }

        postCard.alpha = 0.0f
        motion.post_holder.visibility = View.VISIBLE
        motion.apply {
            updateState(R.id.start, set)
            setTransition(R.id.start, R.id.end)
            setTransitionListener(object : MotionLayout.TransitionListener {
                override fun onTransitionStarted(p0: MotionLayout?, p1: Int, p2: Int) {
                    itemTouchInterceptor.enable()
                    if (p1 == startState) {
                        postCard.alpha = 0.0f
                        motion.post_holder.alpha = 1.0f
                    }
                }

                override fun onTransitionChange(p0: MotionLayout?, p1: Int, p2: Int, p3: Float) {}

                override fun onTransitionCompleted(p0: MotionLayout?, p1: Int) {
                    if (p1 == startState) {
                        itemTouchInterceptor.disable()
                        postCard.alpha = 1.0f
                        motion.post_holder.alpha = 0.0f
                    }
                }

                override fun onTransitionTrigger(
                    p0: MotionLayout?,
                    p1: Int,
                    p2: Boolean,
                    p3: Float
                ) {
                }

            })
            transitionToEnd()
        }
    }
}

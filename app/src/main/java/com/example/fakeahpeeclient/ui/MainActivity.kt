package com.example.fakeahpeeclient.ui

import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.eightbitlab.supportrenderscriptblur.SupportRenderScriptBlur
import com.example.fakeahpeeclient.R
import com.example.fakeahpeeclient.model.Post
import com.example.fakeahpeeclient.singleton.FakeAhPeeClient
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //setSupportActionBar(toolbar)
        initRecycler()
        Log.i("YO", "Activity was created")
        motion.swipeHandler = OnSwipeTouchListener(this@MainActivity, onSwipeDown = {
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
//        toolbar.setOnMenuItemClickListener {
//            when (it.itemId) {
//                R.id.add_post -> {
//                    val i = Intent(this, CreatePostActivity::class.java)
//                    startActivityForResult(i, MAKE_POST_REQUEST)
//                    FakeAhPeeClient.instance.postsAdapter?.notifyDataSetChanged()
//                }
//                R.id.switch_night_mode -> {
//                    AppCompatDelegate.setDefaultNightMode(
//                        if (FakeAhPeeClient.instance.isCurLightTheme)
//                            AppCompatDelegate.MODE_NIGHT_YES
//                        else AppCompatDelegate.MODE_NIGHT_NO
//                    )
//                    FakeAhPeeClient.instance.isCurLightTheme =
//                        !FakeAhPeeClient.instance.isCurLightTheme
//                }
//            }
//            return@setOnMenuItemClickListener true
//        }
        //motion.transitionToState(R.id.start)
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
            FakeAhPeeClient.instance.postsAdapter =
                PostsAdapter(mutableListOf(), this, this, window.decorView)
        recycler_posts.layoutManager = LinearLayoutManager(this)
        recycler_posts.adapter = FakeAhPeeClient.instance.postsAdapter
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
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

        blur.setupWith(motion)
            .setBlurAutoUpdate(true)
            .setBlurAlgorithm(SupportRenderScriptBlur(this))
            .setBlurRadius(25f)
            .setOverlayColor(ResourcesCompat.getColor(resources, R.color.colorOverlay, null))
        postCard.alpha = 0.0f
        motion.post_holder.visibility = View.VISIBLE
        motion.apply {
            updateState(R.id.start, setStart)
            setTransition(R.id.start, R.id.end)
            setTransitionListener(object : MotionLayout.TransitionListener {
                override fun onTransitionStarted(p0: MotionLayout?, p1: Int, p2: Int) {
                    motion.interceptChildren = true
                    if (p1 == startState) {
                        postCard.alpha = 0.0f
                        motion.post_holder.alpha = 1.0f
                    }
                }

                override fun onTransitionChange(p0: MotionLayout?, p1: Int, p2: Int, p3: Float) {}

                override fun onTransitionCompleted(p0: MotionLayout?, p1: Int) {
                    if (p1 == R.id.start) {
                        motion.interceptChildren = false
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

package com.example.fakeahpeeclient.ui

import android.content.Context
import android.util.Log
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import kotlin.math.abs


open class OnSwipeTouchListener(
    ctx: Context?,
    val onSwipeRight: ((e: MotionEvent?) -> Unit)? = null,
    val onSwipeLeft: ((e: MotionEvent?) -> Unit)? = null,
    val onSwipeDown: ((e: MotionEvent?) -> Unit)? = null,
    val onSwipeUp: ((e: MotionEvent?) -> Unit)? = null,
    val onLongPress: ((e: MotionEvent?) -> Unit)? = null
) : OnTouchListener {
    private val gestureDetector: GestureDetector
    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        Log.i("MOTION_TOUCH", "Touch Detected")
        return gestureDetector.onTouchEvent(event)
    }

    private inner class GestureListener : SimpleOnGestureListener() {
        override fun onDown(e: MotionEvent): Boolean {
            return true
        }

        override fun onScroll(
            e1: MotionEvent,
            e2: MotionEvent,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            var result = false
            try {
                val diffY = e2.y - e1.y
                val diffX = e2.x - e1.x
                if (abs(diffX) > abs(diffY)) {
                    if (abs(diffX) > Companion.SWIPE_THRESHOLD && abs(velocityX) > Companion.SWIPE_VELOCITY_THRESHOLD) {
                        if (diffX > 0) {
                            Log.i("MOTION_TOUCH", "SWIPE RIGHT")
                            onSwipeRight?.invoke(e1)
                        } else {
                            Log.i("MOTION_TOUCH", "SWIPE LEFT")
                            onSwipeLeft?.invoke(e1)
                        }
                        result = true
                    }
                } else if (abs(diffY) > Companion.SWIPE_THRESHOLD && abs(velocityY) > Companion.SWIPE_VELOCITY_THRESHOLD) {
                    if (diffY > 0) {
                        Log.i("MOTION_TOUCH", "SWIPE DOWN")
                        onSwipeDown?.invoke(e1)
                    } else {
                        Log.i("MOTION_TOUCH", "SWIPE UP")
                        onSwipeUp?.invoke(e1)
                    }
                    result = true
                }
            } catch (exception: Exception) {
                exception.printStackTrace()
            }
            return result
        }

        override fun onLongPress(e: MotionEvent?) {
            onLongPress?.invoke(e) ?: Unit
        }
    }

    init {
        gestureDetector = GestureDetector(ctx, GestureListener())
    }

    companion object {
        private const val SWIPE_THRESHOLD = 1
        private const val SWIPE_VELOCITY_THRESHOLD = 1
    }
}
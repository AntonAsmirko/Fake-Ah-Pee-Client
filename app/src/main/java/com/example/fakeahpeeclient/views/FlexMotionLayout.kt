package com.example.fakeahpeeclient.views

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.constraintlayout.motion.widget.MotionLayout
import com.example.fakeahpeeclient.ui.OnSwipeTouchListener

class FlexMotionLayout(val c: Context, val attrs: AttributeSet?) : MotionLayout(c, attrs) {

    lateinit var swipeHandler: OnSwipeTouchListener

    var interceptChildren = false

    val callWhileIntercepting: MutableList<(e: MotionEvent) -> Boolean> = mutableListOf()

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (::swipeHandler.isInitialized)
            swipeHandler.onTouch(this, ev)
        return super.dispatchTouchEvent(ev)
    }

    override fun onInterceptTouchEvent(event: MotionEvent?): Boolean {
        var interceptOnce = false
        callWhileIntercepting.forEach { interceptOnce = interceptOnce or it.invoke(event!!) }
        val result = if (!interceptChildren) super.onInterceptTouchEvent(event) else true
        return if (result || interceptOnce) {
            result
        } else {
            onTouchEvent(event)
            result
        }
    }
}
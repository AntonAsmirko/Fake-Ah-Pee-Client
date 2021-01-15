package com.example.fakeahpeeclient.views

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.constraintlayout.motion.widget.MotionLayout
import com.example.fakeahpeeclient.ui.OnSwipeTouchListener

class FlexMotionLayout(val c: Context, val attrs: AttributeSet?) : MotionLayout(c, attrs) {

    lateinit var swipeHandler: OnSwipeTouchListener

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        swipeHandler.onTouch(this, ev)
        return super.dispatchTouchEvent(ev)
    }
}
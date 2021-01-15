package com.example.fakeahpeeclient.views

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import androidx.recyclerview.widget.RecyclerView

class PeacefulRecycler(val c: Context, val attrs: AttributeSet?) : RecyclerView(c, attrs) {

    var intercept = false

    override fun onInterceptTouchEvent(e: MotionEvent?): Boolean {
        if(intercept) return true
        super.onTouchEvent(e)
        return false
    }

}
package com.example.fakeahpeeclient.ui.behaviors

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.math.MathUtils
import androidx.core.view.*
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.appbar.AppBarLayout

class SwipeRefreshLayoutBehavior(context: Context, attrs: AttributeSet?) :
    CoordinatorLayout.Behavior<SwipeRefreshLayout>() {

    var prevDy: Int? = null

    override fun onStartNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: SwipeRefreshLayout,
        directTargetChild: View,
        target: View,
        axes: Int,
        type: Int
    ): Boolean {
        return axes == ViewCompat.SCROLL_AXIS_VERTICAL
    }

    override fun onNestedPreScroll(
        coordinatorLayout: CoordinatorLayout,
        child: SwipeRefreshLayout,
        target: View,
        dx: Int,
        dy: Int,
        consumed: IntArray,
        type: Int
    ) {
        val params = child.layoutParams as CoordinatorLayout.LayoutParams
        params.setMargins(
            child.marginLeft,
            MathUtils.clamp(child.marginTop - dy, 0, 150),
            child.marginRight,
            child.marginBottom
        )
        child.layoutParams = params
        prevDy = dy
        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, type)
    }
}
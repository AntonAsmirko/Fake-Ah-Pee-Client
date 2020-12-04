package com.example.fakeahpeeclient.ui.behaviors

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.math.MathUtils
import androidx.core.view.ViewCompat
import com.google.android.material.appbar.AppBarLayout

class ToolbarBehaviour(context: Context, attrs: AttributeSet?) : CoordinatorLayout.Behavior<AppBarLayout>() {

    override fun onStartNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: AppBarLayout,
        directTargetChild: View,
        target: View,
        axes: Int,
        type: Int
    ): Boolean {
        return axes == ViewCompat.SCROLL_AXIS_VERTICAL
    }

    override fun onNestedPreScroll(
        coordinatorLayout: CoordinatorLayout,
        child: AppBarLayout,
        target: View,
        dx: Int,
        dy: Int,
        consumed: IntArray,
        type: Int
    ) {
        child.translationX = MathUtils.clamp(child.translationX + dy, 0f, child.width.toFloat())
        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, type)
    }
}
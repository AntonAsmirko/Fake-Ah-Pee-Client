package com.example.fakeahpeeclient.extensions

import android.view.MotionEvent
import android.view.View

fun View.isClicked(event: MotionEvent) =
    event.x >= this.x && event.y >= this.y && event.x <= this.x + this.width && event.y <= this.height + this.y
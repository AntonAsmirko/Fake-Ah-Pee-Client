package com.example.fakeahpeeclient.extensions

import android.graphics.Point
import android.view.View

fun View.isLowerDescendingDiagonal(point: Point): Boolean =
    this.height * point.x + this.width * point.y + this.x * (this.y + this.height) - this.y * (this.x + this.width) < 0

fun View.isLowerAscendingDiagonal(point: Point): Boolean =
    this.height * point.x - this.width * point.y + this.x * this.y - (this.x * this.width) * (this.y * this.height) < 0
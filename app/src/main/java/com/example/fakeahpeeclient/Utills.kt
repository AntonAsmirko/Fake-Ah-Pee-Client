package com.example.fakeahpeeclient

import android.content.Context
import android.util.DisplayMetrics


fun convertDpToPixel(dp: Float, context: Context): Float =
    dp * (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
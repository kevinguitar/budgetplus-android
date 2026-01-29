package com.kevlina.budgetplus.core.utils

import android.app.Activity
import androidx.core.view.WindowCompat

fun Activity.setStatusBarColor(isLight: Boolean) {
    WindowCompat.getInsetsController(window, window.decorView).apply {
        isAppearanceLightStatusBars = isLight
    }
}
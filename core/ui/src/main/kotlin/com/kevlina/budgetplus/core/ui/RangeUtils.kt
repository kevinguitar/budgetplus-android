package com.kevlina.budgetplus.core.ui

fun lerp(start: Float, stop: Float, fraction: Float) =
    (1 - fraction) * start + fraction * stop
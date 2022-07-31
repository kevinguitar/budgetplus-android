package com.kevlina.budgetplus.utils

import android.app.Activity
import kotlin.reflect.KClass

data class NavigationInfo(
    val destination: KClass<out Activity>,
    val finishCurrent: Boolean = true
)
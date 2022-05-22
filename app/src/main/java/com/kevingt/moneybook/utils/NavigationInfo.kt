package com.kevingt.moneybook.utils

import android.app.Activity
import kotlin.reflect.KClass

data class NavigationInfo(
    val destination: KClass<out Activity>,
    val finishCurrent: Boolean = false
)
package com.kevlina.budgetplus.core.common.nav

import android.app.Activity
import android.os.Bundle
import kotlin.reflect.KClass

data class NavigationInfo(
    val destination: KClass<out Activity>,
    val bundle: Bundle = Bundle.EMPTY,
    val finishCurrent: Boolean = true,
)
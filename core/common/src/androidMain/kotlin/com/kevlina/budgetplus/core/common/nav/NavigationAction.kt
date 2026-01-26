package com.kevlina.budgetplus.core.common.nav

import android.content.Intent

data class NavigationAction(
    val intent: Intent,
    val finishCurrent: Boolean = true,
)
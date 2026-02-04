package com.kevlina.budgetplus.core.common.nav

import android.content.Intent

data class AndroidNavigationAction(
    val intent: Intent,
    val finishCurrent: Boolean = true,
) : NavigationAction {
    override fun navigate() = Unit
}
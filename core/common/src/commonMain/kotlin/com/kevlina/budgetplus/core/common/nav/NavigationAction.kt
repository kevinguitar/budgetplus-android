package com.kevlina.budgetplus.core.common.nav

import com.kevlina.budgetplus.core.common.MutableEventFlow

fun interface NavigationAction {
    fun navigate()
}

typealias NavigationFlow = MutableEventFlow<NavigationAction>

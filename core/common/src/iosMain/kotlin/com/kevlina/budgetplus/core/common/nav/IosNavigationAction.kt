package com.kevlina.budgetplus.core.common.nav

data class IosNavigationAction(
    private val onNavigate: () -> Unit,
) : NavigationAction {
    override fun navigate() = onNavigate()
}

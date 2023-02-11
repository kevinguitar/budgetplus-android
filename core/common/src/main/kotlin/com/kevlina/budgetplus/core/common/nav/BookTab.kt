package com.kevlina.budgetplus.core.common.nav

enum class BookTab {
    Add, History;

    val route get() = name.navRoute
}

enum class AddDest {
    Record, EditCategory, UnlockPremium;

    val route get() = name.navRoute
}

enum class HistoryDest {
    Overview, Records;

    val route get() = name.navRoute
}

private val String.navRoute get() = replaceFirstChar { it.lowercaseChar() }
package com.kevlina.budgetplus.book

enum class BookTab {
    Add, History;

    val route get() = name
}

enum class AddDest {
    Record, EditCategory, UnlockPremium;

    val route get() = name
}

enum class HistoryDest {
    Overview, Details;

    val route get() = name
}
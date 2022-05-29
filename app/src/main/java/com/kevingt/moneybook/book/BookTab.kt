package com.kevingt.moneybook.book

enum class BookTab {
    Add, Overview;

    val route get() = name
}

enum class AddDest {
    Record, EditCategory;

    val route get() = name
}

enum class OverviewDest {
    One;

    val route get() = name
}
package com.kevingt.moneybook.book

enum class BookTab {
    Record, Overview;

    val route get() = name
}
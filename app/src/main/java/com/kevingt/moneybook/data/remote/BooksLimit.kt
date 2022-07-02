package com.kevingt.moneybook.data.remote

const val BOOKS_LIMIT: Int = 5

class ExceedBooksLimitException : IllegalStateException("You have exceed $BOOKS_LIMIT books limit")
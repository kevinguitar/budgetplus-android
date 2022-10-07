package com.kevlina.budgetplus.data.remote

import androidx.annotation.StringRes

const val FREE_BOOKS_LIMIT: Int = 1
const val PREMIUM_BOOKS_LIMIT: Int = 10

sealed class JoinBookException : IllegalStateException() {

    class ExceedFreeLimit(@StringRes val errorRes: Int) : JoinBookException()

    object JoinInfoNotFound : JoinBookException()

    class General(@StringRes val errorRes: Int) : JoinBookException()
}
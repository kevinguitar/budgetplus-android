package com.kevlina.budgetplus.core.data

import androidx.annotation.StringRes

const val FREE_BOOKS_LIMIT: Int = 1
const val PREMIUM_BOOKS_LIMIT: Int = 10

sealed class JoinBookException : IllegalStateException() {

    class ExceedFreeLimit(@StringRes val errorRes: Int) : JoinBookException()

    class JoinInfoNotFound(override val message: String) : JoinBookException()

    class General(@StringRes val errorRes: Int) : JoinBookException()
}
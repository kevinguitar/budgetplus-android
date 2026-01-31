package com.kevlina.budgetplus.core.data

const val FREE_BOOKS_LIMIT: Int = 1
const val PREMIUM_BOOKS_LIMIT: Int = 10

sealed class JoinBookException : IllegalStateException() {

    class ExceedFreeLimit(override val message: String) : JoinBookException()

    class JoinInfoNotFound(override val message: String) : JoinBookException()

    class General(override val message: String) : JoinBookException()
}
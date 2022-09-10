package com.kevlina.budgetplus.data.remote

import androidx.annotation.StringRes

const val FREE_BOOKS_LIMIT: Int = 1
const val PREMIUM_BOOKS_LIMIT: Int = 10

open class JoinBookException(@StringRes open val errorRes: Int) : IllegalStateException()
class ExceedFreeLimitException(@StringRes override val errorRes: Int) : JoinBookException(errorRes)
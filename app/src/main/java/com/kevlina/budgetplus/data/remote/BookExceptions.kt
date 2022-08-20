package com.kevlina.budgetplus.data.remote

import androidx.annotation.StringRes

const val FREE_BOOKS_LIMIT: Int = 1
const val PREMIUM_BOOKS_LIMIT: Int = 10

class JoinBookException(@StringRes val errorRes: Int) : IllegalStateException()
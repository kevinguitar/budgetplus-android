package com.kevlina.budgetplus.data.remote

const val FREE_BOOKS_LIMIT: Int = 1
const val PREMIUM_BOOKS_LIMIT: Int = 10

class ExceedFreeBooksLimitException : IllegalStateException()
class ExceedPremiumBooksLimitException : IllegalStateException()
class JoinLinkExpiredException : IllegalStateException()
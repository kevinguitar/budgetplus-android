package com.kevlina.budgetplus.core.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class JoinInfo(
    val bookId: String = "",
    val generatedOn: Long = 0L,
)
package com.kevlina.budgetplus.core.data.remote

import kotlinx.serialization.Serializable
import kotlin.time.Clock

@Serializable
data class Book(
    val id: String = "",
    val name: String = "",
    val ownerId: String = "",
    val authors: List<String> = emptyList(),
    val createdOn: Long = Clock.System.now().toEpochMilliseconds(),
    val expenseCategories: List<String> = emptyList(),
    val incomeCategories: List<String> = emptyList(),
    val currencyCode: String? = null,
    val archived: Boolean = false,
    val archivedOn: Long? = null,
    val allowMembersEdit: Boolean? = null,
)
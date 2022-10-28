package com.kevlina.budgetplus.core.data.remote

import com.kevlina.budgetplus.core.common.RecordType
import kotlinx.serialization.Serializable

@Serializable
data class Record(
    val id: String = "",
    val type: RecordType = RecordType.Expense,
    val date: Long = 0,
    val category: String = "",
    val name: String = "",
    val price: Double = 0.0,
    val author: Author? = null
)

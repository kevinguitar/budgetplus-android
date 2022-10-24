package com.kevlina.budgetplus.data.remote

import com.kevlina.budgetplus.core.data.Author
import kotlinx.serialization.Serializable

enum class RecordType {
    Expense, Income
}

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

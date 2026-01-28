package com.kevlina.budgetplus.core.data.remote

import androidx.compose.runtime.Immutable
import com.kevlina.budgetplus.core.common.RecordType
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class Record(
    val id: String = "",
    val type: RecordType = RecordType.Expense,
    val category: String = "",
    val name: String = "",
    val price: Double = 0.0,
    val author: Author? = null,
    val date: Long = 0,
    val timestamp: Long? = null,
    val batchId: String? = null,
)

val Record.createdOn: Long
    get() = timestamp ?: LocalDate.fromEpochDays(date.toInt())
        .atStartOfDayIn(TimeZone.UTC)
        .epochSeconds

val Record.isBatched: Boolean
    get() = batchId != null

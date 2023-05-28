package com.kevlina.budgetplus.core.data.remote

import androidx.compose.runtime.Stable
import com.google.firebase.firestore.Exclude
import com.kevlina.budgetplus.core.common.RecordType
import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset

@Stable
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
) {

    @get:Exclude
    val createdOn: Long
        get() = timestamp ?: LocalDateTime
            .of(LocalDate.ofEpochDay(date), LocalTime.MIN)
            .toEpochSecond(ZoneOffset.UTC)

    @get:Exclude
    val isBatched: Boolean
        get() = batchId != null
}

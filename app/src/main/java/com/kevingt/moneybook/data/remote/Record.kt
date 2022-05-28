package com.kevingt.moneybook.data.remote

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

enum class RecordType {
    Expense, Income
}

@Parcelize
data class Record(
    val type: RecordType,
    val date: Long,
    val category: String,
    val name: String,
    val price: Double
) : Parcelable

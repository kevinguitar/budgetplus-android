package com.kevingt.moneybook.data.remote

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

enum class RecordType {
    Expense, Income
}

@Parcelize
data class Record(
    val type: RecordType = RecordType.Expense,
    val date: Long = 0,
    val category: String = "",
    val name: String = "",
    val price: Double = 0.0
) : Parcelable

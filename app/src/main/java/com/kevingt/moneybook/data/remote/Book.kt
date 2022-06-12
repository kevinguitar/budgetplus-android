package com.kevingt.moneybook.data.remote

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Book(
    val name: String = "",
    val ownerId: String = "",
    val authors: List<String> = emptyList(),
    val expenseCategories: List<String> = emptyList(),
    val incomeCategories: List<String> = emptyList(),
) : Parcelable
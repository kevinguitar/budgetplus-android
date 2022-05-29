package com.kevingt.moneybook.data.remote

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Book(
    val name: String = "",
    val authors: List<Author> = emptyList(),
    val expenseCategories: List<String> = emptyList(),
    val incomeCategories: List<String> = emptyList(),
) : Parcelable
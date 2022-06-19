package com.kevingt.moneybook.data.remote

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Book(
    val id: String = "",
    val name: String = "",
    val ownerId: String = "",
    val authors: List<String> = emptyList(),
    val createdOn: Long = System.currentTimeMillis(),
    val expenseCategories: List<String> = emptyList(),
    val incomeCategories: List<String> = emptyList(),
    val archived: Boolean = false
) : Parcelable
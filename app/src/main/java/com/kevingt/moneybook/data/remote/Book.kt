package com.kevingt.moneybook.data.remote

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Book(
    val id: String = "",
    val users: List<String> = emptyList(),
    val categories: List<String> = emptyList(),
) : Parcelable
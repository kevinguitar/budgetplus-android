package com.kevingt.moneybook.data.remote

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    val id: String = "",
    val name: String? = null,
    val email: String? = null,
    val photoUrl: String? = null,
    val premium: Boolean? = false,
    val hideAds: Boolean? = false
) : Parcelable

@Parcelize
data class Author(
    val id: String = "",
    val name: String? = null,
) : Parcelable

fun User.toAuthor() = Author(id = id, name = name)
package com.kevlina.budgetplus.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String = "",
    val name: String? = null,
    val email: String? = null,
    val photoUrl: String? = null,
    val premium: Boolean? = false,
    val hideAds: Boolean? = false
)

@Serializable
data class Author(
    val id: String = "",
    val name: String? = null,
)

fun User.toAuthor() = Author(id = id, name = name)
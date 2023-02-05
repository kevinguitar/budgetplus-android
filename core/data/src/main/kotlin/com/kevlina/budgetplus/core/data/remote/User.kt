package com.kevlina.budgetplus.core.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String = "",
    val name: String? = null,
    val email: String? = null,
    val photoUrl: String? = null,
    val premium: Boolean? = null,
    val createdOn: Long? = null,
    val lastActiveOn: Long? = null,
    val fcmToken: String? = null,
    val language: String? = null,
)

@Serializable
data class Author(
    val id: String = "",
    val name: String? = null,
)

fun User.toAuthor() = Author(id = id, name = name)
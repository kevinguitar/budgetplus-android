package com.kevlina.budgetplus.core.data

import kotlinx.coroutines.flow.StateFlow

interface AuthManager {

    val userState: StateFlow<User?>
    val isPremium: StateFlow<Boolean>

    fun requireUserId(): String

    suspend fun renameUser(newName: String)

    fun markPremium()

    fun logout()

}
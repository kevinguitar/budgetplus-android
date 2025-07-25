package com.kevlina.budgetplus.core.data

import com.kevlina.budgetplus.core.data.remote.User
import kotlinx.coroutines.flow.StateFlow

interface AuthManager {

    val userState: StateFlow<User?>
    val isPremium: StateFlow<Boolean>

    val userId: String?
    fun requireUserId(): String

    suspend fun renameUser(newName: String)

    fun markPremium()

    fun updateFcmToken(newToken: String)

    fun logout()

}
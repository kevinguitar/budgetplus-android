package com.kevlina.budgetplus.core.data

import com.kevlina.budgetplus.core.data.remote.User
import kotlinx.coroutines.flow.MutableStateFlow

class FakeAuthManager(
    user: User? = null,
    isPremium: Boolean = false,
    override val userId: String? = user?.id
) : AuthManager {

    override val userState = MutableStateFlow(user)
    override val isPremium = MutableStateFlow(isPremium)

    override fun requireUserId(): String = requireNotNull(userId) { "User id is null" }

    override suspend fun renameUser(newName: String) {
        error("Not yet implemented")
    }

    override fun markPremium() {
        error("Not yet implemented")
    }

    override fun updateFcmToken(newToken: String) {
        error("Not yet implemented")
    }

    override fun logout() {
        error("Not yet implemented")
    }
}
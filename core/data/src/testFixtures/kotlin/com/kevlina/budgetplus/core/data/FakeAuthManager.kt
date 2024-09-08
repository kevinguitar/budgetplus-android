package com.kevlina.budgetplus.core.data

import com.kevlina.budgetplus.core.data.remote.User
import kotlinx.coroutines.flow.MutableStateFlow

class FakeAuthManager(
    user: User? = null,
    isPremium: Boolean = false,
) : AuthManager {

    override val userState = MutableStateFlow(user)
    override val isPremium = MutableStateFlow(isPremium)

    override fun requireUserId(): String {
        error("Not yet implemented")
    }

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
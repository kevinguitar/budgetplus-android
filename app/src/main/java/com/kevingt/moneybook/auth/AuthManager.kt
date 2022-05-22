package com.kevingt.moneybook.auth

import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.kevingt.moneybook.data.local.PreferenceHolder
import com.kevingt.moneybook.data.local.bindObject
import com.kevingt.moneybook.data.remote.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

interface AuthManager {

    val userState: StateFlow<User?>

    fun requireUserId(): String

    fun setUser(user: User?)

    fun logout()

}

@Singleton
class AuthManagerImpl @Inject constructor(
    preferenceHolder: PreferenceHolder,
) : AuthManager {

    private var currentUser by preferenceHolder.bindObject<User>()

    private val _userState = MutableStateFlow(currentUser)
    override val userState: StateFlow<User?> get() = _userState

    override fun requireUserId(): String {
        return requireNotNull(userState.value?.id) { "User id is null" }
    }

    override fun setUser(user: User?) {
        _userState.value = user
        currentUser = user
    }

    override fun logout() {
        setUser(null)
        Firebase.auth.signOut()
    }
}
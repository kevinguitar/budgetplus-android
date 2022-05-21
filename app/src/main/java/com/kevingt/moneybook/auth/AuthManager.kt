package com.kevingt.moneybook.auth

import com.google.firebase.auth.UserInfo
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.kevingt.moneybook.data.local.PreferenceHolder
import com.kevingt.moneybook.data.local.bindObject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

interface AuthManager {

    val userState: StateFlow<UserInfo?>

    fun setUser(userInfo: UserInfo?)

    fun logout()

}

class AuthManagerImpl @Inject constructor(
    preferenceHolder: PreferenceHolder,
) : AuthManager {

    private var currentUser by preferenceHolder.bindObject<UserInfo>()

    private val _userState = MutableStateFlow(currentUser)
    override val userState: StateFlow<UserInfo?> get() = _userState

    override fun setUser(userInfo: UserInfo?) {
        _userState.value = userInfo
        currentUser = userInfo
    }

    override fun logout() {
        setUser(null)
        Firebase.auth.signOut()
    }
}
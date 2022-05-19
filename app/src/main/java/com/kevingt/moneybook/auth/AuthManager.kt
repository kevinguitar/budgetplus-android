package com.kevingt.moneybook.auth

import android.content.Context
import com.google.firebase.auth.UserInfo
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.kevingt.moneybook.R
import com.kevingt.moneybook.utils.AppScope
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

interface AuthManager {

    val user: StateFlow<UserInfo?>

    fun setUser(userInfo: UserInfo)

    fun logout()

}

class AuthManagerImpl @Inject constructor(
    @ApplicationContext context: Context,
    @AppScope private val scope: CoroutineScope,
) : AuthManager {

    private val keyUser = "user"
    private val preference = context.getSharedPreferences(
        context.getString(R.string.app_name) + "_auth",
        Context.MODE_PRIVATE
    )

    private val _user = MutableStateFlow<UserInfo?>(null)
    override val user: StateFlow<UserInfo?> get() = _user

    init {
        scope.launch(Dispatchers.IO) {
            val json = preference.getString(keyUser, null)
            _user.value = try {
                Gson().fromJson(json, UserInfo::class.java)
            } catch (e: Exception) {
                Timber.e(e, "Paring user profile failed")
                null
            }
        }
    }

    override fun setUser(userInfo: UserInfo) {
        _user.value = userInfo

        scope.launch(Dispatchers.IO) {
            val json = Gson().toJson(user)
            preference.edit().putString(keyUser, json).apply()
        }
    }

    override fun logout() {
        _user.value = null
        Firebase.auth.signOut()
    }
}
package com.kevingt.moneybook.auth

import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import javax.inject.Inject

class AuthManager @Inject constructor() {

    fun logout() {
        Firebase.auth.signOut()
    }
}
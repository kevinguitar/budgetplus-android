package com.kevingt.moneybook.book

import android.content.Intent
import androidx.lifecycle.ViewModel
import com.kevingt.moneybook.auth.AuthActivity
import com.kevingt.moneybook.auth.AuthManager
import com.kevingt.moneybook.data.remote.BookRepo
import com.kevingt.moneybook.utils.NavigationFlow
import com.kevingt.moneybook.utils.NavigationInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class BookViewModel @Inject constructor(
    private val authManager: AuthManager,
    private val bookRepo: BookRepo,
) : ViewModel() {

    val navigation = NavigationFlow()

    private fun navigateToAuth() {
        val navInfo = NavigationInfo(destination = AuthActivity::class)
        navigation.tryEmit(navInfo)
    }

    fun logout() {
        authManager.logout()
        navigateToAuth()
    }

    fun handleIntent(intent: Intent?) {
        val uri = intent?.data ?: return
        if (uri.pathSegments.firstOrNull() == "join") {
            bookRepo.setPendingJoinRequest(uri)
        }
    }

}
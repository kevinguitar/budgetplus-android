package com.kevlina.budgetplus.book.menu.vm

import android.app.Activity
import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kevlina.budgetplus.core.common.NavigationFlow
import com.kevlina.budgetplus.core.common.NavigationInfo
import com.kevlina.budgetplus.core.common.Toaster
import com.kevlina.budgetplus.core.common.combineState
import com.kevlina.budgetplus.core.common.sendEvent
import com.kevlina.budgetplus.core.data.AuthManager
import com.kevlina.budgetplus.core.data.BookRepo
import com.kevlina.budgetplus.core.data.Tracker
import com.kevlina.budgetplus.feature.auth.AuthActivity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named
import kotlin.reflect.KClass

@HiltViewModel
class BookMenuViewModel @Inject constructor(
    private val bookRepo: BookRepo,
    private val authManager: AuthManager,
    private val toaster: Toaster,
    private val tracker: Tracker,
    @Named("auth") private val authDest: KClass<out Activity>,
) : ViewModel() {

    val navigation = NavigationFlow()

    val isBookOwner = bookRepo.bookState.combineState(
        other = authManager.userState,
        scope = viewModelScope
    ) { book, user ->
        book != null && book.ownerId == user?.id
    }

    val isPremium = authManager.isPremium

    val currentUsername get() = authManager.userState.value?.name
    val currentBookName get() = bookRepo.bookState.value?.name

    fun renameUser(newName: String) {
        viewModelScope.launch {
            try {
                authManager.renameUser(newName)
                tracker.logEvent("user_renamed")
            } catch (e: Exception) {
                toaster.showError(e)
            }
        }
    }

    fun renameBook(newName: String) {
        viewModelScope.launch {
            try {
                bookRepo.renameBook(newName)
            } catch (e: Exception) {
                toaster.showError(e)
            }
        }
    }

    fun deleteOrLeave() {
        viewModelScope.launch {
            try {
                bookRepo.leaveOrDeleteBook()
            } catch (e: Exception) {
                toaster.showError(e)
            }
        }
    }

    fun logout() {
        authManager.logout()
        tracker.logEvent("logout")

        val navInfo = NavigationInfo(
            destination = authDest,
            bundle = Bundle().apply { putBoolean(AuthActivity.ARG_ENABLE_ONE_TAP, false) }
        )
        navigation.sendEvent(navInfo)
    }
}
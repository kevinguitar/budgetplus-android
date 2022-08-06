package com.kevlina.budgetplus.book.menu.vm

import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kevlina.budgetplus.auth.AuthActivity
import com.kevlina.budgetplus.auth.AuthManager
import com.kevlina.budgetplus.data.remote.BookRepo
import com.kevlina.budgetplus.utils.NavigationFlow
import com.kevlina.budgetplus.utils.NavigationInfo
import com.kevlina.budgetplus.utils.Toaster
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookMenuViewModel @Inject constructor(
    private val bookRepo: BookRepo,
    private val authManager: AuthManager,
    private val toaster: Toaster,
) : ViewModel() {

    val navigation = NavigationFlow()

    val isBookOwner = combine(
        bookRepo.bookState,
        authManager.userState
    ) { book, user ->
        book != null && book.ownerId == user?.id
    }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)

    val currentUsername get() = authManager.userState.value?.name

    val currentBookName get() = bookRepo.bookState.value?.name

    fun renameUser(newName: String) {
        viewModelScope.launch {
            try {
                authManager.renameUser(newName)
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

        val navInfo = NavigationInfo(
            destination = AuthActivity::class,
            bundle = Bundle().apply { putBoolean(AuthActivity.ARG_ENABLE_ONE_TAP, false) }
        )
        navigation.tryEmit(navInfo)
    }
}
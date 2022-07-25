package com.kevingt.budgetplus.book.menu.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kevingt.budgetplus.auth.AuthActivity
import com.kevingt.budgetplus.auth.AuthManager
import com.kevingt.budgetplus.data.remote.BookRepo
import com.kevingt.budgetplus.utils.NavigationFlow
import com.kevingt.budgetplus.utils.NavigationInfo
import com.kevingt.budgetplus.utils.Toaster
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

        val navInfo = NavigationInfo(destination = AuthActivity::class)
        navigation.tryEmit(navInfo)
    }
}
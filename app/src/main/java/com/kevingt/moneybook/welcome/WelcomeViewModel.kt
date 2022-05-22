package com.kevingt.moneybook.welcome

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kevingt.moneybook.book.BookActivity
import com.kevingt.moneybook.data.remote.BookRepo
import com.kevingt.moneybook.utils.NavigationFlow
import com.kevingt.moneybook.utils.NavigationInfo
import com.kevingt.moneybook.utils.Toaster
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WelcomeViewModel @Inject constructor(
    private val bookRepo: BookRepo,
    private val toaster: Toaster,
) : ViewModel() {

    val navigation = NavigationFlow()

    fun createBook(name: String) {
        viewModelScope.launch {
            try {
                bookRepo.createBook(name)
                toaster.showMessage("Book created!")

                val navInfo = NavigationInfo(destination = BookActivity::class)
                navigation.tryEmit(navInfo)
            } catch (e: Exception) {
                toaster.showError(e)
            }
        }
    }

    fun handleJoinRequest() {
        if (!bookRepo.hasPendingJoinRequest) return

        viewModelScope.launch {
            try {
                bookRepo.handlePendingJoinRequest()
                toaster.showMessage("Joined book!")

                val navInfo = NavigationInfo(destination = BookActivity::class)
                navigation.tryEmit(navInfo)
            } catch (e: Exception) {
                toaster.showError(e)
            }
        }
    }
}
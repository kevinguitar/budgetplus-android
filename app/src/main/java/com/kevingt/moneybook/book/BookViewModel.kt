package com.kevingt.moneybook.book

import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kevingt.moneybook.data.remote.BookRepo
import com.kevingt.moneybook.utils.NavigationFlow
import com.kevingt.moneybook.utils.NavigationInfo
import com.kevingt.moneybook.utils.Toaster
import com.kevingt.moneybook.welcome.WelcomeActivity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookViewModel @Inject constructor(
    private val bookRepo: BookRepo,
    private val toaster: Toaster,
) : ViewModel() {

    val navigation = NavigationFlow()

    init {
        if (bookRepo.currentBookId != null) {
            bookRepo.bookState
                .onEach { book ->
                    if (book == null) {
                        val nav = NavigationInfo(destination = WelcomeActivity::class)
                        navigation.tryEmit(nav)
                    }
                }
                .launchIn(viewModelScope)
        }
    }

    fun handleIntent(intent: Intent?) {
        val uri = intent?.data ?: return
        if (uri.pathSegments.firstOrNull() == "join") {
            bookRepo.setPendingJoinRequest(uri)
        }
    }

    fun handleJoinRequest() {
        if (!bookRepo.hasPendingJoinRequest) return

        viewModelScope.launch {
            try {
                bookRepo.handlePendingJoinRequest()
                toaster.showMessage("Joined book!")
            } catch (e: Exception) {
                toaster.showError(e)
            }
        }
    }

}
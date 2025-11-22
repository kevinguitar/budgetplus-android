package com.kevlina.budgetplus.feature.welcome

import androidx.compose.foundation.text.input.TextFieldState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kevlina.budgetplus.core.common.R
import com.kevlina.budgetplus.core.common.SnackbarSender
import com.kevlina.budgetplus.core.common.StringProvider
import com.kevlina.budgetplus.core.common.Toaster
import com.kevlina.budgetplus.core.common.nav.NavigationAction
import com.kevlina.budgetplus.core.common.nav.NavigationFlow
import com.kevlina.budgetplus.core.common.sendEvent
import com.kevlina.budgetplus.core.data.AuthManager
import com.kevlina.budgetplus.core.data.BookRepo
import com.kevlina.budgetplus.core.data.JoinBookException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named

class WelcomeViewModel @Inject constructor(
    val navigation: NavigationFlow,
    val snackbarSender: SnackbarSender,
    private val bookRepo: BookRepo,
    private val authManager: AuthManager,
    private val toaster: Toaster,
    private val stringProvider: StringProvider,
    @Named("book") private val bookNavigationAction: NavigationAction,
    @Named("logout") private val logoutNavigationAction: NavigationAction,
) : ViewModel() {

    private var createBookJob: Job? = null

    val bookName = TextFieldState()

    private val _isCreatingBook = MutableStateFlow(false)
    val isCreatingBook = _isCreatingBook.asStateFlow()

    fun createBook() {
        if (createBookJob?.isActive == true) {
            return
        }

        createBookJob = viewModelScope.launch {
            _isCreatingBook.value = true
            val name = bookName.text.toString()
            try {
                bookRepo.createBook(name = name, source = "welcome")
                toaster.showMessage(stringProvider[R.string.book_create_success, name])

                navigation.sendEvent(bookNavigationAction)
            } catch (e: Exception) {
                snackbarSender.sendError(e)
            } finally {
                _isCreatingBook.value = false
            }
        }
    }

    fun handleJoinRequest() {
        if (!bookRepo.hasPendingJoinRequest) return

        viewModelScope.launch {
            try {
                val bookName = bookRepo.handlePendingJoinRequest() ?: return@launch
                toaster.showMessage(stringProvider[R.string.book_join_success, bookName])

                navigation.sendEvent(bookNavigationAction)
            } catch (e: JoinBookException.General) {
                snackbarSender.send(e.errorRes)
            } catch (e: JoinBookException.JoinInfoNotFound) {
                Timber.e(e)
            } catch (e: Exception) {
                snackbarSender.sendError(e)
            }
        }
    }

    fun logout() {
        authManager.logout()
        navigation.sendEvent(logoutNavigationAction)
    }
}
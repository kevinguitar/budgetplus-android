package com.kevlina.budgetplus.welcome

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kevlina.budgetplus.R
import com.kevlina.budgetplus.book.BookActivity
import com.kevlina.budgetplus.data.remote.BookRepo
import com.kevlina.budgetplus.data.remote.JoinBookException
import com.kevlina.budgetplus.utils.*
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WelcomeViewModel @Inject constructor(
    private val bookRepo: BookRepo,
    private val toaster: Toaster,
    private val tracker: Tracker,
    @ApplicationContext private val context: Context,
) : ViewModel() {

    val navigation = NavigationFlow()

    private var createBookJob: Job? = null

    fun createBook(name: String) {
        if (createBookJob?.isActive == true) {
            return
        }

        createBookJob = viewModelScope.launch {
            try {
                bookRepo.createBook(name)
                toaster.showMessage(context.getString(R.string.book_create_success, name))
                tracker.logEvent("book_created_from_welcome")

                val navInfo = NavigationInfo(destination = BookActivity::class)
                navigation.sendEvent(navInfo)
            } catch (e: Exception) {
                toaster.showError(e)
            }
        }
    }

    fun handleJoinRequest() {
        if (!bookRepo.hasPendingJoinRequest) return

        viewModelScope.launch {
            try {
                val bookName = bookRepo.handlePendingJoinRequest()
                toaster.showMessage(context.getString(R.string.book_join_success, bookName))

                val navInfo = NavigationInfo(destination = BookActivity::class)
                navigation.sendEvent(navInfo)
            } catch (e: JoinBookException) {
                toaster.showMessage(e.errorRes)
            } catch (e: Exception) {
                toaster.showError(e)
            }
        }
    }
}
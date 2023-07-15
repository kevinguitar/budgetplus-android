package com.kevlina.budgetplus.feature.welcome

import android.app.Activity
import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kevlina.budgetplus.core.common.R
import com.kevlina.budgetplus.core.common.StringProvider
import com.kevlina.budgetplus.core.common.Toaster
import com.kevlina.budgetplus.core.common.Tracker
import com.kevlina.budgetplus.core.common.nav.NavigationFlow
import com.kevlina.budgetplus.core.common.nav.NavigationInfo
import com.kevlina.budgetplus.core.common.sendEvent
import com.kevlina.budgetplus.core.data.BookRepo
import com.kevlina.budgetplus.core.data.JoinBookException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named
import kotlin.reflect.KClass

@HiltViewModel
@Stable
class WelcomeViewModel @Inject constructor(
    private val bookRepo: BookRepo,
    private val toaster: Toaster,
    private val tracker: Tracker,
    private val stringProvider: StringProvider,
    @Named("book") private val bookDest: KClass<out Activity>,
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
                toaster.showMessage(stringProvider[R.string.book_create_success, name])
                tracker.logEvent("book_created_from_welcome")

                val navInfo = NavigationInfo(destination = bookDest)
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
                val bookName = bookRepo.handlePendingJoinRequest() ?: return@launch
                toaster.showMessage(stringProvider[R.string.book_join_success, bookName])

                val navInfo = NavigationInfo(destination = bookDest)
                navigation.sendEvent(navInfo)
            } catch (e: JoinBookException.General) {
                toaster.showMessage(e.errorRes)
            } catch (e: JoinBookException.JoinInfoNotFound) {
                Timber.e(e)
            } catch (e: Exception) {
                toaster.showError(e)
            }
        }
    }
}
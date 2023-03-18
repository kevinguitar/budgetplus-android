package com.kevlina.budgetplus.book

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kevlina.budgetplus.core.common.MutableEventFlow
import com.kevlina.budgetplus.core.common.R
import com.kevlina.budgetplus.core.common.Toaster
import com.kevlina.budgetplus.core.common.nav.NavigationFlow
import com.kevlina.budgetplus.core.common.nav.NavigationInfo
import com.kevlina.budgetplus.core.common.sendEvent
import com.kevlina.budgetplus.core.data.BookRepo
import com.kevlina.budgetplus.core.data.JoinBookException
import com.kevlina.budgetplus.feature.welcome.WelcomeActivity
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
@Stable
class BookViewModel @Inject constructor(
    private val bookRepo: BookRepo,
    private val toaster: Toaster,
    @ApplicationContext private val context: Context,
) : ViewModel() {

    val navigation = NavigationFlow()

    val unlockPremiumEvent = MutableEventFlow<Unit>()

    init {
        if (bookRepo.currentBookId != null) {
            bookRepo.bookState
                .onEach { book ->
                    if (book == null) {
                        val nav = NavigationInfo(destination = WelcomeActivity::class)
                        navigation.sendEvent(nav)
                    }
                }
                .launchIn(viewModelScope)
        }
    }

    fun handleJoinIntent(intent: Intent?): Boolean {
        val uri = intent?.data ?: return false
        return if (uri.pathSegments.firstOrNull() == "join") {
            bookRepo.setPendingJoinRequest(uri.lastPathSegment)
            true
        } else {
            false
        }
    }

    fun handleJoinRequest() {
        if (!bookRepo.hasPendingJoinRequest) return

        viewModelScope.launch {
            try {
                val bookName = bookRepo.handlePendingJoinRequest()
                toaster.showMessage(context.getString(R.string.book_join_success, bookName))
            } catch (e: JoinBookException.ExceedFreeLimit) {
                unlockPremiumEvent.sendEvent()
                toaster.showMessage(e.errorRes)
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
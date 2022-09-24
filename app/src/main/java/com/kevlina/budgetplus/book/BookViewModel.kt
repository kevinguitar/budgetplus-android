package com.kevlina.budgetplus.book

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kevlina.budgetplus.R
import com.kevlina.budgetplus.data.remote.BookRepo
import com.kevlina.budgetplus.data.remote.ExceedFreeLimitException
import com.kevlina.budgetplus.data.remote.JoinBookException
import com.kevlina.budgetplus.utils.MutableEventFlow
import com.kevlina.budgetplus.utils.NavigationFlow
import com.kevlina.budgetplus.utils.NavigationInfo
import com.kevlina.budgetplus.utils.Toaster
import com.kevlina.budgetplus.utils.sendEvent
import com.kevlina.budgetplus.welcome.WelcomeActivity
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
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

    fun handleIntent(intent: Intent?) {
        val uri = intent?.data ?: return
        if (uri.pathSegments.firstOrNull() == "join") {
            bookRepo.setPendingJoinRequest(uri.lastPathSegment)
        }
    }

    fun handleJoinRequest() {
        if (!bookRepo.hasPendingJoinRequest) return

        viewModelScope.launch {
            try {
                val bookName = bookRepo.handlePendingJoinRequest()
                toaster.showMessage(context.getString(R.string.book_join_success, bookName))
            } catch (e: ExceedFreeLimitException) {
                unlockPremiumEvent.sendEvent()
                toaster.showMessage(e.errorRes)
            } catch (e: JoinBookException) {
                toaster.showMessage(e.errorRes)
            } catch (e: Exception) {
                toaster.showError(e)
            }
        }
    }
}
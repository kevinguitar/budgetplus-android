package com.kevlina.budgetplus.app.book

import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kevlina.budgetplus.core.common.EventFlow
import com.kevlina.budgetplus.core.common.MutableEventFlow
import com.kevlina.budgetplus.core.common.R
import com.kevlina.budgetplus.core.common.SnackbarSender
import com.kevlina.budgetplus.core.common.StringProvider
import com.kevlina.budgetplus.core.common.mapState
import com.kevlina.budgetplus.core.common.nav.NAV_JOIN_PATH
import com.kevlina.budgetplus.core.common.nav.NavigationAction
import com.kevlina.budgetplus.core.common.nav.NavigationFlow
import com.kevlina.budgetplus.core.common.sendEvent
import com.kevlina.budgetplus.core.data.AdMobInitializer
import com.kevlina.budgetplus.core.data.AuthManager
import com.kevlina.budgetplus.core.data.BookRepo
import com.kevlina.budgetplus.core.data.JoinBookException
import com.kevlina.budgetplus.core.theme.ThemeManager
import com.kevlina.budgetplus.core.ui.bubble.BubbleViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class BookViewModel @Inject constructor(
    val snackbarSender: SnackbarSender,
    val themeManager: ThemeManager,
    val navigation: NavigationFlow,
    val bubbleViewModel: BubbleViewModel,
    private val bookRepo: BookRepo,
    private val stringProvider: StringProvider,
    @Named("welcome") private val welcomeNavigationAction: NavigationAction,
    adMobInitializer: AdMobInitializer,
    authManager: AuthManager,
) : ViewModel() {

    private val _unlockPremiumEvent = MutableEventFlow<Unit>()
    val unlockPremiumEvent: EventFlow<Unit> get() = _unlockPremiumEvent

    val showAds = authManager.isPremium.mapState { !it }
    val isAdMobInitialized = adMobInitializer.isInitialized

    init {
        if (bookRepo.currentBookId != null) {
            bookRepo.bookState
                .onEach { book ->
                    if (book == null) {
                        navigation.sendEvent(welcomeNavigationAction)
                    }
                }
                .launchIn(viewModelScope)
        }
    }

    fun handleJoinIntent(intent: Intent): Boolean {
        val uri = intent.data ?: return false
        return if (uri.pathSegments.firstOrNull() == NAV_JOIN_PATH) {
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
                val bookName = bookRepo.handlePendingJoinRequest() ?: return@launch
                snackbarSender.send(stringProvider[R.string.book_join_success, bookName])
            } catch (e: JoinBookException.ExceedFreeLimit) {
                _unlockPremiumEvent.sendEvent()
                snackbarSender.send(e.errorRes, canDismiss = true)
            } catch (e: JoinBookException.General) {
                snackbarSender.send(e.errorRes, canDismiss = true)
            } catch (e: JoinBookException.JoinInfoNotFound) {
                Timber.e(e)
            } catch (e: Exception) {
                snackbarSender.sendError(e)
            }
        }
    }
}
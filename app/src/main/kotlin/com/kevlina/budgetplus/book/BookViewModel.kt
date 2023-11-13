package com.kevlina.budgetplus.book

import android.content.Intent
import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kevlina.budgetplus.core.common.EventFlow
import com.kevlina.budgetplus.core.common.MutableEventFlow
import com.kevlina.budgetplus.core.common.R
import com.kevlina.budgetplus.core.common.StringProvider
import com.kevlina.budgetplus.core.common.Toaster
import com.kevlina.budgetplus.core.common.mapState
import com.kevlina.budgetplus.core.common.nav.NavigationFlow
import com.kevlina.budgetplus.core.common.nav.NavigationInfo
import com.kevlina.budgetplus.core.common.sendEvent
import com.kevlina.budgetplus.core.data.AuthManager
import com.kevlina.budgetplus.core.data.BookRepo
import com.kevlina.budgetplus.core.data.JoinBookException
import com.kevlina.budgetplus.core.data.RemoteConfig
import com.kevlina.budgetplus.core.theme.ThemeManager
import com.kevlina.budgetplus.feature.welcome.WelcomeActivity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
@Stable
class BookViewModel @Inject constructor(
    val snackbarSender: BookSnackbarSender,
    val themeManager: ThemeManager,
    private val bookRepo: BookRepo,
    private val toaster: Toaster,
    private val stringProvider: StringProvider,
    authManager: AuthManager,
    remoteConfig: RemoteConfig,
) : ViewModel() {

    val navigation = NavigationFlow()

    private val _unlockPremiumEvent = MutableEventFlow<Unit>()
    val unlockPremiumEvent: EventFlow<Unit> get() = _unlockPremiumEvent

    val showAds = authManager.isPremium.mapState { !it }
    val bannerAdId = remoteConfig.observeString("ad_banner_mode", "default")
        .mapState { value ->
            when (value) {
                "refresh_30_sec" -> R.string.admob_banner_id_30sec
                "refresh_60_sec" -> R.string.admob_banner_id_60sec
                else -> R.string.admob_banner_id_auto
            }
        }

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
                val bookName = bookRepo.handlePendingJoinRequest() ?: return@launch
                toaster.showMessage(stringProvider[R.string.book_join_success, bookName])
            } catch (e: JoinBookException.ExceedFreeLimit) {
                _unlockPremiumEvent.sendEvent()
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
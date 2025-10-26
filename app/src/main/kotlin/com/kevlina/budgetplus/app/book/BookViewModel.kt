package com.kevlina.budgetplus.app.book

import android.content.Intent
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kevlina.budgetplus.core.ads.AdMobInitializer
import com.kevlina.budgetplus.core.common.R
import com.kevlina.budgetplus.core.common.SnackbarSender
import com.kevlina.budgetplus.core.common.StringProvider
import com.kevlina.budgetplus.core.common.nav.BookDest
import com.kevlina.budgetplus.core.common.nav.BottomNavTab
import com.kevlina.budgetplus.core.common.nav.NAV_JOIN_PATH
import com.kevlina.budgetplus.core.common.nav.NavController
import com.kevlina.budgetplus.core.common.nav.NavigationAction
import com.kevlina.budgetplus.core.common.nav.NavigationFlow
import com.kevlina.budgetplus.core.common.sendEvent
import com.kevlina.budgetplus.core.data.AuthManager
import com.kevlina.budgetplus.core.data.BookRepo
import com.kevlina.budgetplus.core.data.JoinBookException
import com.kevlina.budgetplus.core.theme.ThemeManager
import com.kevlina.budgetplus.core.ui.bubble.BubbleViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
internal class BookViewModel @Inject constructor(
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

    val navController = NavController(startRoot = BottomNavTab.Add.root)
    private val currentNavKeyFlow = snapshotFlow { navController.backStack.lastOrNull() }.filterNotNull()

    val showAds = combine(
        authManager.isPremium,
        currentNavKeyFlow
    ) { isPremium, currentNavKey ->
        !isPremium && currentNavKey != BookDest.UnlockPremium
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)

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

        currentNavKeyFlow
            .onEach { key ->
                // Clear the preview colors if the user navigates out of the picker screen.
                if (key != BookDest.Colors) {
                    themeManager.clearPreviewColors()
                }
            }
            .launchIn(viewModelScope)
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
                navController.navigate(BookDest.UnlockPremium)
                snackbarSender.send(e.errorRes)
            } catch (e: JoinBookException.General) {
                snackbarSender.send(e.errorRes)
            } catch (e: JoinBookException.JoinInfoNotFound) {
                Timber.e(e)
            } catch (e: Exception) {
                snackbarSender.sendError(e)
            }
        }
    }
}
package com.kevlina.budgetplus.app.book

import android.content.Intent
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import budgetplus.core.common.generated.resources.Res
import budgetplus.core.common.generated.resources.book_join_success
import co.touchlab.kermit.Logger
import com.kevlina.budgetplus.core.ads.AdMobInitializer
import com.kevlina.budgetplus.core.ads.AdUnitId
import com.kevlina.budgetplus.core.common.SnackbarSender
import com.kevlina.budgetplus.core.common.di.ViewModelKey
import com.kevlina.budgetplus.core.common.di.ViewModelScope
import com.kevlina.budgetplus.core.common.nav.BookDest
import com.kevlina.budgetplus.core.common.nav.BottomNavTab
import com.kevlina.budgetplus.core.common.nav.NAV_COLORS_PATH
import com.kevlina.budgetplus.core.common.nav.NAV_JOIN_PATH
import com.kevlina.budgetplus.core.common.nav.NAV_OVERVIEW_PATH
import com.kevlina.budgetplus.core.common.nav.NAV_RECORD_PATH
import com.kevlina.budgetplus.core.common.nav.NAV_SETTINGS_PATH
import com.kevlina.budgetplus.core.common.nav.NAV_UNLOCK_PREMIUM_PATH
import com.kevlina.budgetplus.core.common.nav.NavController
import com.kevlina.budgetplus.core.common.nav.NavigationAction
import com.kevlina.budgetplus.core.common.nav.NavigationFlow
import com.kevlina.budgetplus.core.common.sendEvent
import com.kevlina.budgetplus.core.data.AuthManager
import com.kevlina.budgetplus.core.data.BookRepo
import com.kevlina.budgetplus.core.data.JoinBookException
import com.kevlina.budgetplus.core.theme.ThemeManager
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Named
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString

@ViewModelKey(BookViewModel::class)
@ContributesIntoMap(ViewModelScope::class)
class BookViewModel(
    val snackbarSender: SnackbarSender,
    val themeManager: ThemeManager,
    val navigation: NavigationFlow,
    val bubbleViewModel: BubbleViewModel,
    private val bookRepo: BookRepo,
    @Named("welcome") private val welcomeNavigationAction: NavigationAction,
    private val adUnitId: AdUnitId,
    adMobInitializer: AdMobInitializer,
    authManager: AuthManager,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    val navController = NavController(
        startRoot = BottomNavTab.Add.root,
        serializer = BookDest.serializer(),
        savedStateHandle = savedStateHandle
    )
    private val currentNavKeyFlow = snapshotFlow { navController.backStack.lastOrNull() }.filterNotNull()

    val showAds = combine(
        authManager.isPremium,
        currentNavKeyFlow
    ) { isPremium, currentNavKey ->
        !isPremium && currentNavKey != BookDest.UnlockPremium
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)

    val isAdMobInitialized = adMobInitializer.isInitialized

    val bannerAdUnitId get() = adUnitId.banner

    init {
        if (!bookRepo.booksState.value.isNullOrEmpty()) {
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

    fun handleIntent(intent: Intent) {
        val uri = intent.data ?: return
        return when (val firstSegment = uri.pathSegments.firstOrNull()) {
            NAV_JOIN_PATH -> bookRepo.setPendingJoinRequest(uri.lastPathSegment)
            NAV_RECORD_PATH -> navController.navigate(BookDest.Record)
            NAV_OVERVIEW_PATH -> navController.navigate(BookDest.Overview)
            NAV_UNLOCK_PREMIUM_PATH -> navController.navigate(BookDest.UnlockPremium)
            NAV_SETTINGS_PATH -> {
                val showMembers = uri.getQueryParameter("showMembers")?.toBoolean() ?: false
                navController.navigate(BookDest.Settings(showMembers = showMembers))
            }

            NAV_COLORS_PATH -> {
                val hex = uri.getQueryParameter("hex")
                navController.navigate(BookDest.Colors(hex = hex))
            }

            else -> Logger.d { "Deeplink: Unknown segment $firstSegment. Url=$uri" }
        }
    }

    fun handleJoinRequest() {
        if (!bookRepo.hasPendingJoinRequest) return

        viewModelScope.launch {
            try {
                val bookName = bookRepo.handlePendingJoinRequest() ?: return@launch
                snackbarSender.send(getString(Res.string.book_join_success, bookName))
            } catch (e: JoinBookException.ExceedFreeLimit) {
                navController.navigate(BookDest.UnlockPremium)
                snackbarSender.send(e.message)
            } catch (e: JoinBookException.General) {
                snackbarSender.send(e.message)
            } catch (e: JoinBookException.JoinInfoNotFound) {
                Logger.e(e) { "JoinInfo not found in DB" }
            } catch (e: Exception) {
                snackbarSender.sendError(e)
            }
        }
    }
}
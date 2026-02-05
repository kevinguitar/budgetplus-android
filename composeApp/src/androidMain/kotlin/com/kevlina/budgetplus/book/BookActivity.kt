package com.kevlina.budgetplus.book

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import budgetplus.core.common.generated.resources.Res
import budgetplus.core.common.generated.resources.app_update_downloaded
import budgetplus.core.common.generated.resources.cta_complete
import co.touchlab.kermit.Logger
import com.kevlina.budgetplus.book.ui.BookBinding
import com.kevlina.budgetplus.core.common.SnackbarDuration
import com.kevlina.budgetplus.core.common.SnackbarSender
import com.kevlina.budgetplus.core.common.di.ViewModelGraphProvider
import com.kevlina.budgetplus.core.common.di.resolveGraphExtensionFactory
import com.kevlina.budgetplus.core.common.nav.APP_DEEPLINK
import com.kevlina.budgetplus.core.common.nav.NAV_SETTINGS_PATH
import com.kevlina.budgetplus.core.common.nav.consumeNavigation
import com.kevlina.budgetplus.core.data.AuthManager
import com.kevlina.budgetplus.core.data.BookRepo
import com.kevlina.budgetplus.core.theme.ThemeManager
import com.kevlina.budgetplus.core.ui.AppTheme
import com.kevlina.budgetplus.core.utils.LocalViewModelGraphProvider
import com.kevlina.budgetplus.core.utils.setStatusBarColor
import com.kevlina.budgetplus.feature.auth.AuthActivity
import com.kevlina.budgetplus.feature.welcome.WelcomeActivity
import com.kevlina.budgetplus.inapp.update.InAppUpdateManager
import com.kevlina.budgetplus.inapp.update.InAppUpdateState
import dev.zacsweers.metro.Inject

class BookActivity : ComponentActivity() {

    @Inject private lateinit var authManager: AuthManager
    @Inject private lateinit var bookRepo: BookRepo
    @Inject private lateinit var themeManager: ThemeManager
    @Inject private lateinit var inAppUpdateManager: InAppUpdateManager
    @Inject private lateinit var snackbarSender: SnackbarSender
    @Inject private lateinit var viewModelGraphProvider: ViewModelGraphProvider

    private val viewModel by viewModels<BookViewModel>(
        factoryProducer = ::viewModelGraphProvider
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        resolveGraphExtensionFactory<BookActivityGraph.Factory>()
            .create(this)
            .inject(this)

        // When the user open the settings from app preference.
        if (intent.action == Intent.ACTION_APPLICATION_PREFERENCES) {
            intent.data = "$APP_DEEPLINK/$NAV_SETTINGS_PATH".toUri()
        }

        enableEdgeToEdge()
        setStatusBarColor(isLight = false)
        super.onCreate(savedInstanceState)

        viewModel.handleIntent(intent)

        Logger.d { "DEBUGG: BookActivity, bookId=${bookRepo.currentBookId}" }
        val destination = when {
            authManager.userState.value == null -> AuthActivity::class.java
            bookRepo.currentBookId == null -> WelcomeActivity::class.java
            else -> null
        }

        if (destination != null) {
            startActivity(Intent(this, destination))
            finish()
        }

        setContent {
            CompositionLocalProvider(LocalViewModelGraphProvider provides viewModelGraphProvider) {
                val themeColors by themeManager.themeColors.collectAsStateWithLifecycle()
                AppTheme(themeColors) {
                    BookBinding(vm = viewModel)
                }

                val appUpdateState by inAppUpdateManager.updateState.collectAsStateWithLifecycle()
                LaunchedEffect(key1 = appUpdateState) {
                    if (appUpdateState is InAppUpdateState.Downloaded) {
                        snackbarSender.send(
                            message = Res.string.app_update_downloaded,
                            actionLabel = Res.string.cta_complete,
                            duration = SnackbarDuration.Indefinite,
                            action = (appUpdateState as InAppUpdateState.Downloaded).complete
                        )
                    }
                }
            }
        }

        consumeNavigation(viewModel.navigation)

        addOnNewIntentListener { newIntent ->
            viewModel.handleIntent(newIntent)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.handleJoinRequest()
    }
}
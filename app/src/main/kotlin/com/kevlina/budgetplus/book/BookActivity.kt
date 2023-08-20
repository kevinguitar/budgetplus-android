package com.kevlina.budgetplus.book

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kevlina.budgetplus.book.ui.BookBinding
import com.kevlina.budgetplus.core.common.R
import com.kevlina.budgetplus.core.common.nav.APP_DEEPLINK
import com.kevlina.budgetplus.core.common.nav.ARG_URL
import com.kevlina.budgetplus.core.common.nav.AddDest
import com.kevlina.budgetplus.core.data.AuthManager
import com.kevlina.budgetplus.core.data.BookRepo
import com.kevlina.budgetplus.core.ui.AppTheme
import com.kevlina.budgetplus.core.ui.Book
import com.kevlina.budgetplus.core.ui.SnackbarData
import com.kevlina.budgetplus.core.ui.SnackbarDuration
import com.kevlina.budgetplus.core.ui.SnackbarSender
import com.kevlina.budgetplus.feature.auth.AuthActivity
import com.kevlina.budgetplus.feature.records.RecordsViewModel
import com.kevlina.budgetplus.feature.welcome.WelcomeActivity
import com.kevlina.budgetplus.inapp.update.InAppUpdateManager
import com.kevlina.budgetplus.inapp.update.InAppUpdateState
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.components.ActivityComponent
import javax.inject.Inject

@AndroidEntryPoint
class BookActivity : ComponentActivity() {

    @Inject lateinit var authManager: AuthManager
    @Inject lateinit var bookRepo: BookRepo
    @Inject lateinit var inAppUpdateManager: InAppUpdateManager
    @Inject @Book lateinit var snackbarSender: SnackbarSender

    private val viewModel by viewModels<BookViewModel>()

    private var newIntent: Intent? by mutableStateOf(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        // When the app is in the background, the fcm won't go through the service impl, so we need
        // to check the intent extras manually, if the url is presented, simply pass it as data.
        val url = intent.extras?.getString(ARG_URL)
        when {
            url != null -> intent.data = url.toUri()

            // When the user open the settings from app preference.
            intent.action == Intent.ACTION_APPLICATION_PREFERENCES -> {
                intent.data = "$APP_DEEPLINK/${AddDest.Settings.route}".toUri()
            }
        }
        super.onCreate(savedInstanceState)

        viewModel.handleJoinIntent(intent)

        val destination = when {
            authManager.userState.value == null -> AuthActivity::class.java
            bookRepo.currentBookId == null -> WelcomeActivity::class.java
            else -> null
        }

        if (destination != null) {
            startActivity(Intent(this, destination).setAction(Intent.ACTION_VIEW))
            finish()
        }

        setContent {

            AppTheme {
                BookBinding(
                    vm = viewModel,
                    newIntent = newIntent,
                )
            }

            val appUpdateState by inAppUpdateManager.updateState.collectAsStateWithLifecycle()
            LaunchedEffect(key1 = appUpdateState) {
                if (appUpdateState is InAppUpdateState.Downloaded) {
                    snackbarSender.showSnackbar(SnackbarData(
                        message = getString(R.string.app_update_downloaded),
                        actionLabel = getString(R.string.cta_complete),
                        duration = SnackbarDuration.Long,
                        action = (appUpdateState as InAppUpdateState.Downloaded).complete
                    ))
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (!viewModel.handleJoinIntent(intent)) {
            newIntent = intent
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.handleJoinRequest()
    }

    @EntryPoint
    @InstallIn(ActivityComponent::class)
    interface VmFactoryProvider {
        fun recordsVmFactory(): RecordsViewModel.Factory
    }
}
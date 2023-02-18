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
import androidx.navigation.compose.rememberNavController
import com.kevlina.budgetplus.core.common.nav.ARG_URL
import com.kevlina.budgetplus.core.data.AuthManager
import com.kevlina.budgetplus.core.data.BookRepo
import com.kevlina.budgetplus.core.ui.AppTheme
import com.kevlina.budgetplus.feature.auth.AuthActivity
import com.kevlina.budgetplus.feature.records.vm.RecordsViewModel
import com.kevlina.budgetplus.feature.welcome.WelcomeActivity
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.components.ActivityComponent
import javax.inject.Inject

@AndroidEntryPoint
class BookActivity : ComponentActivity() {

    @Inject
    lateinit var authManager: AuthManager

    @Inject
    lateinit var bookRepo: BookRepo

    private val viewModel by viewModels<BookViewModel>()

    private var newIntent: Intent? by mutableStateOf(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        // When the app is in the background, the fcm won't go through the service impl, so we need
        // to check the intent extras manually, if the url is presented, simply pass it as data.
        val url = intent.extras?.getString(ARG_URL)
        if (url != null) {
            intent.data = url.toUri()
        }
        super.onCreate(savedInstanceState)

        viewModel.handleJoinIntent(intent)

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
            val navController = rememberNavController()
            LaunchedEffect(newIntent) {
                navController.handleDeepLink(newIntent)
            }

            AppTheme {
                BookBinding(viewModel = viewModel)
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
        fun detailsVmFactory(): RecordsViewModel.Factory
    }
}
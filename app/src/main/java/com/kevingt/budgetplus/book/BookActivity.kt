package com.kevingt.budgetplus.book

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.google.android.gms.ads.MobileAds
import com.kevingt.budgetplus.auth.AuthActivity
import com.kevingt.budgetplus.auth.AuthManager
import com.kevingt.budgetplus.data.remote.BookRepo
import com.kevingt.budgetplus.ui.AppTheme
import com.kevingt.budgetplus.welcome.WelcomeActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class BookActivity : ComponentActivity() {

    @Inject
    lateinit var authManager: AuthManager

    @Inject
    lateinit var bookRepo: BookRepo

    private val viewModel by viewModels<BookViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.handleIntent(intent)

        val destination = when {
            authManager.userState.value == null -> AuthActivity::class.java
            bookRepo.currentBookId == null -> WelcomeActivity::class.java
            else -> null
        }

        if (destination != null) {
            startActivity(Intent(this, destination))
            finish()
        }

        MobileAds.initialize(this)

        setContent {
            AppTheme {
                BookBinding(viewModel = viewModel)
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        viewModel.handleIntent(intent)
    }

    override fun onResume() {
        super.onResume()
        viewModel.handleJoinRequest()
    }
}
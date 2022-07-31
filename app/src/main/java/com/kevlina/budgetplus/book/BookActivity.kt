package com.kevlina.budgetplus.book

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.kevlina.budgetplus.auth.AuthActivity
import com.kevlina.budgetplus.auth.AuthManager
import com.kevlina.budgetplus.data.remote.BookRepo
import com.kevlina.budgetplus.ui.AppTheme
import com.kevlina.budgetplus.welcome.WelcomeActivity
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
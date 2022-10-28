package com.kevlina.budgetplus.auth

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.kevlina.budgetplus.auth.ui.AuthBinding
import com.kevlina.budgetplus.core.ui.AppTheme
import dagger.Lazy
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AuthActivity : ComponentActivity() {

    @Inject
    lateinit var viewModel: Lazy<AuthViewModel>

    private val enableOneTap by lazy { intent.extras?.getBoolean(ARG_ENABLE_ONE_TAP) ?: true }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AppTheme {
                AuthBinding(viewModel = viewModel.get())
            }
        }

        if (enableOneTap) {
            viewModel.get().checkAuthorizedAccounts()
        }
    }

    @Deprecated("Deprecated in Java")
    @Suppress("DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        viewModel.get().onActivityResult(requestCode, resultCode, data)
    }

    companion object {
        const val ARG_ENABLE_ONE_TAP = "enable_one_tap"
    }
}
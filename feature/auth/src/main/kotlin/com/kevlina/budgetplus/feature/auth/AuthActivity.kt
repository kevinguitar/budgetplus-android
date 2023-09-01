package com.kevlina.budgetplus.feature.auth

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kevlina.budgetplus.core.common.nav.ARG_ENABLE_ONE_TAP
import com.kevlina.budgetplus.core.theme.ThemeManager
import com.kevlina.budgetplus.core.ui.AppTheme
import com.kevlina.budgetplus.feature.auth.ui.AuthBinding
import dagger.Lazy
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AuthActivity : ComponentActivity() {

    @Inject lateinit var themeManager: ThemeManager
    @Inject lateinit var viewModel: Lazy<AuthViewModel>

    private val enableOneTap by lazy { intent.extras?.getBoolean(ARG_ENABLE_ONE_TAP) ?: true }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val colorTone by themeManager.colorTone.collectAsStateWithLifecycle()
            AppTheme(colorTone) {
                AuthBinding(vm = viewModel.get())
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
}
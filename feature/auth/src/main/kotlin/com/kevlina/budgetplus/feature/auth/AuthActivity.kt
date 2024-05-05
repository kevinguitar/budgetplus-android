package com.kevlina.budgetplus.feature.auth

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kevlina.budgetplus.core.common.nav.ARG_AUTO_SIGN_IN
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

    private val enableAutoSignIn by lazy { intent.extras?.getBoolean(ARG_AUTO_SIGN_IN) ?: true }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val themeColors by themeManager.themeColors.collectAsStateWithLifecycle()
            AppTheme(themeColors) {
                AuthBinding(viewModel.get())
            }
        }

        viewModel.get().checkAuthorizedAccounts(enableAutoSignIn)
    }
}
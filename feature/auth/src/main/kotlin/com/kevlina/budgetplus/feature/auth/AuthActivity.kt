package com.kevlina.budgetplus.feature.auth

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kevlina.budgetplus.core.common.nav.consumeNavigation
import com.kevlina.budgetplus.core.theme.ThemeManager
import com.kevlina.budgetplus.core.ui.AppTheme
import com.kevlina.budgetplus.core.utils.setStatusBarColor
import com.kevlina.budgetplus.feature.auth.ui.AuthBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AuthActivity : ComponentActivity() {

    @Inject lateinit var themeManager: ThemeManager
    @Inject lateinit var viewModel: AuthViewModel

    private val enableAutoSignIn by lazy { intent.extras?.getBoolean(ARG_ENABLE_AUTO_SIGN_IN) ?: true }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        setStatusBarColor(isLight = false)
        super.onCreate(savedInstanceState)

        setContent {
            val themeColors by themeManager.themeColors.collectAsStateWithLifecycle()
            AppTheme(themeColors) {
                AuthBinding(vm = viewModel)
            }
        }

        viewModel.checkAuthorizedAccounts(enableAutoSignIn)

        consumeNavigation(viewModel.navigation)
    }

    companion object {
        internal const val ARG_ENABLE_AUTO_SIGN_IN = "enable_auto_sign_in"
    }
}
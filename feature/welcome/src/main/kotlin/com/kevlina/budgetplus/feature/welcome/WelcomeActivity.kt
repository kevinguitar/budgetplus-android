package com.kevlina.budgetplus.feature.welcome

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kevlina.budgetplus.core.common.di.resolveGraphExtensionFactory
import com.kevlina.budgetplus.core.common.nav.consumeNavigation
import com.kevlina.budgetplus.core.theme.ThemeManager
import com.kevlina.budgetplus.core.ui.AppTheme
import com.kevlina.budgetplus.core.utils.setStatusBarColor
import com.kevlina.budgetplus.feature.welcome.ui.WelcomeBinding
import dagger.hilt.android.AndroidEntryPoint
import dev.zacsweers.metro.Inject

@AndroidEntryPoint
class WelcomeActivity : ComponentActivity() {

    @Inject lateinit var themeManager: ThemeManager

    private val viewModel by viewModels<WelcomeViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        resolveGraphExtensionFactory<WelcomeActivityGraph.Factory>()
            .create(this)
            .inject(this)

        enableEdgeToEdge()
        setStatusBarColor(isLight = true)
        super.onCreate(savedInstanceState)

        setContent {
            val themeColors by themeManager.themeColors.collectAsStateWithLifecycle()
            AppTheme(themeColors) {
                WelcomeBinding(viewModel)
            }
        }

        consumeNavigation(viewModel.navigation)
    }

    override fun onResume() {
        super.onResume()
        viewModel.handleJoinRequest()
    }

}
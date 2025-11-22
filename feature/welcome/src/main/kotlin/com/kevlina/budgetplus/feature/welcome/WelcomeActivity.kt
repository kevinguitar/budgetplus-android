package com.kevlina.budgetplus.feature.welcome

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kevlina.budgetplus.core.common.di.ViewModelGraphProvider
import com.kevlina.budgetplus.core.common.di.resolveGraphExtensionFactory
import com.kevlina.budgetplus.core.common.nav.consumeNavigation
import com.kevlina.budgetplus.core.theme.ThemeManager
import com.kevlina.budgetplus.core.ui.AppTheme
import com.kevlina.budgetplus.core.utils.LocalViewModelGraphProvider
import com.kevlina.budgetplus.core.utils.setStatusBarColor
import com.kevlina.budgetplus.feature.welcome.ui.WelcomeBinding
import dev.zacsweers.metro.Inject

class WelcomeActivity : ComponentActivity() {

    @Inject lateinit var themeManager: ThemeManager
    @Inject lateinit var viewModelGraphProvider: ViewModelGraphProvider

    private val viewModel by viewModels<WelcomeViewModel>(
        factoryProducer = ::viewModelGraphProvider
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        resolveGraphExtensionFactory<WelcomeActivityGraph.Factory>()
            .create(this)
            .inject(this)

        enableEdgeToEdge()
        setStatusBarColor(isLight = true)
        super.onCreate(savedInstanceState)

        setContent {
            CompositionLocalProvider(LocalViewModelGraphProvider provides viewModelGraphProvider) {
                val themeColors by themeManager.themeColors.collectAsStateWithLifecycle()
                AppTheme(themeColors) {
                    WelcomeBinding(viewModel)
                }
            }
        }

        consumeNavigation(viewModel.navigation)
    }

    override fun onResume() {
        super.onResume()
        viewModel.handleJoinRequest()
    }

}
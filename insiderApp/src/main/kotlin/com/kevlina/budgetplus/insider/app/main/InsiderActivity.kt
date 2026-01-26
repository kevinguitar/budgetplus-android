package com.kevlina.budgetplus.insider.app.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import com.kevlina.budgetplus.core.common.di.ViewModelGraphProvider
import com.kevlina.budgetplus.core.common.di.resolveGraphExtensionFactory
import com.kevlina.budgetplus.core.data.AuthManager
import com.kevlina.budgetplus.core.ui.AppTheme
import com.kevlina.budgetplus.core.utils.LocalViewModelGraphProvider
import com.kevlina.budgetplus.core.utils.setStatusBarColor
import com.kevlina.budgetplus.feature.auth.AuthActivity
import com.kevlina.budgetplus.insider.app.main.ui.InsiderBinding
import dev.zacsweers.metro.Inject

class InsiderActivity : ComponentActivity() {

    @Inject lateinit var authManager: AuthManager
    @Inject lateinit var viewModelGraphProvider: ViewModelGraphProvider

    override fun onCreate(savedInstanceState: Bundle?) {
        resolveGraphExtensionFactory<InsiderActivityGraph.Factory>()
            .create(this)
            .inject(this)

        enableEdgeToEdge()
        setStatusBarColor(isLight = false)
        super.onCreate(savedInstanceState)

        if (authManager.userState.value == null) {
            startActivity(Intent(this, AuthActivity::class.java))
            finish()
        }

        setContent {
            CompositionLocalProvider(LocalViewModelGraphProvider provides viewModelGraphProvider) {
                AppTheme {
                    InsiderBinding()
                }
            }
        }
    }
}
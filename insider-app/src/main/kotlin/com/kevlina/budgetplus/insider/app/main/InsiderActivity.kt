package com.kevlina.budgetplus.insider.app.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.kevlina.budgetplus.core.data.AuthManager
import com.kevlina.budgetplus.core.ui.AppTheme
import com.kevlina.budgetplus.core.utils.setStatusBarColor
import com.kevlina.budgetplus.feature.auth.AuthActivity
import com.kevlina.budgetplus.insider.app.main.ui.InsiderBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class InsiderActivity : ComponentActivity() {

    @Inject lateinit var authManager: AuthManager

    private val viewModel by viewModels<InsiderViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        setStatusBarColor(isLight = false)
        super.onCreate(savedInstanceState)

        if (authManager.userState.value == null){
            startActivity(Intent(this, AuthActivity::class.java))
            finish()
        }

        setContent {
            AppTheme {
                InsiderBinding(vm = viewModel)
            }
        }
    }
}
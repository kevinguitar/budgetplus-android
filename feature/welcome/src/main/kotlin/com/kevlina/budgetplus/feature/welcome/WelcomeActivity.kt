package com.kevlina.budgetplus.feature.welcome

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.kevlina.budgetplus.core.ui.AppTheme
import com.kevlina.budgetplus.feature.welcome.ui.WelcomeBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WelcomeActivity : ComponentActivity() {

    private val viewModel by viewModels<WelcomeViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AppTheme {
                WelcomeBinding(viewModel)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.handleJoinRequest()
    }

}
package com.kevingt.budgetplus.welcome

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.kevingt.budgetplus.ui.AppTheme
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
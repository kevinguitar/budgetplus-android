package com.kevlina.budgetplus.feature.welcome.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.kevlina.budgetplus.feature.welcome.WelcomeViewModel

@Composable
fun WelcomeContentWide(viewModel: WelcomeViewModel) {

    Row(
        modifier = Modifier.fillMaxSize()
    ) {

        CreateBookBlock(
            createBook = viewModel::createBook,
            isWideMode = true,
            applyStatusBarPadding = true,
            applyNavBarPadding = true,
            modifier = Modifier
                .fillMaxHeight()
                .weight(1F)
        )

        CollabBlock(
            applyStatusBarPadding = true,
            applyNavBarPadding = true,
            modifier = Modifier
                .fillMaxHeight()
                .weight(1F)
        )
    }
}
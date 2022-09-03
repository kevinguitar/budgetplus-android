package com.kevlina.budgetplus.welcome.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.kevlina.budgetplus.welcome.WelcomeViewModel

@Composable
fun WelcomeContentWide(viewModel: WelcomeViewModel) {

    Row(
        modifier = Modifier.fillMaxSize()
    ) {

        CreateBookBlock(
            createBook = viewModel::createBook,
            isWideMode = true,
            modifier = Modifier
                .fillMaxHeight()
                .weight(1F)
        )

        CollabBlock(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1F)
        )
    }
}
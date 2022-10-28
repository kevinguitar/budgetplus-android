package com.kevlina.budgetplus.feature.welcome.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.kevlina.budgetplus.feature.welcome.WelcomeViewModel

@Composable
fun WelcomeContentRegular(viewModel: WelcomeViewModel) {

    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        CreateBookBlock(
            createBook = viewModel::createBook,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1F)
        )

        CollabBlock(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1F)
        )
    }
}
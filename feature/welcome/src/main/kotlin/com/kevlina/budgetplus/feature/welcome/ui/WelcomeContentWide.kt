package com.kevlina.budgetplus.feature.welcome.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kevlina.budgetplus.feature.welcome.WelcomeViewModel

@Composable
fun WelcomeContentWide(viewModel: WelcomeViewModel) {
    val isCreatingBook by viewModel.isCreatingBook.collectAsStateWithLifecycle()

    Row(
        modifier = Modifier.fillMaxSize()
    ) {
        CreateBookBlock(
            bookName = viewModel.bookName,
            isCreatingBook = isCreatingBook,
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
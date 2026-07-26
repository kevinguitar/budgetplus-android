package com.kevlina.budgetplus.feature.welcome.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kevlina.budgetplus.core.ui.AppTheme

@Composable
fun WelcomeContentRegular(state: WelcomeState) {
    val isCreatingBook by state.isCreatingBook.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        CreateBookBlock(
            bookName = state.bookName,
            isCreatingBook = isCreatingBook,
            createBook = state.createBook,
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

@Preview
@Composable
private fun WelcomeContentRegular_Preview() = AppTheme {
    WelcomeContentRegular(WelcomeState.preview)
}

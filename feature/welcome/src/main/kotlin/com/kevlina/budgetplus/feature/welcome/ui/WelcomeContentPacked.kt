package com.kevlina.budgetplus.feature.welcome.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kevlina.budgetplus.feature.welcome.WelcomeViewModel

@Composable
fun WelcomeContentPacked(viewModel: WelcomeViewModel) {
    val isCreatingBook by viewModel.isCreatingBook.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        CreateBookBlock(
            bookName = viewModel.bookName,
            isCreatingBook = isCreatingBook,
            createBook = viewModel::createBook,
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        )

        CollabBlock(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        )
    }
}
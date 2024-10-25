package com.kevlina.budgetplus.feature.welcome.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kevlina.budgetplus.feature.welcome.WelcomeViewModel

@Composable
fun WelcomeContentRegular(viewModel: WelcomeViewModel) {

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        val bookName by viewModel.bookName.collectAsStateWithLifecycle()

        CreateBookBlock(
            bookName = bookName,
            onBookNameChange = viewModel::setBookName,
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
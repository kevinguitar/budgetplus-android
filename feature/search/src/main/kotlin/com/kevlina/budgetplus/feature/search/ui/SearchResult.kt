package com.kevlina.budgetplus.feature.search.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.kevlina.budgetplus.core.theme.LocalAppColors
import com.kevlina.budgetplus.core.theme.ThemeColors
import com.kevlina.budgetplus.core.ui.AppTheme
import com.kevlina.budgetplus.feature.record.card.RecordCard
import com.kevlina.budgetplus.feature.record.card.RecordCardUiState

@Composable
internal fun SearchResult(
    modifier: Modifier = Modifier,
    result: List<RecordCardUiState>,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize()
    ) {
        //TODO: Zero case

        items(result) { recordState ->
            RecordCard(
                recordState
            )
        }
    }
}

@Preview
@Composable
private fun SearchResult_Preview() = AppTheme(ThemeColors.Dusk) {
    SearchResult(
        modifier = Modifier.background(LocalAppColors.current.light),
        result = listOf(
            RecordCardUiState.preview,
            RecordCardUiState.preview,
            RecordCardUiState.preview,
            RecordCardUiState.preview,
            RecordCardUiState.preview,
            RecordCardUiState.preview,
            RecordCardUiState.preview,
            RecordCardUiState.preview,
            RecordCardUiState.preview,
            RecordCardUiState.preview,
            RecordCardUiState.preview,
            RecordCardUiState.preview,
        )
    )
}
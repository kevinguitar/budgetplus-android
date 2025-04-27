package com.kevlina.budgetplus.feature.search.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kevlina.budgetplus.core.common.R
import com.kevlina.budgetplus.core.theme.LocalAppColors
import com.kevlina.budgetplus.core.theme.ThemeColors
import com.kevlina.budgetplus.core.ui.AppTheme
import com.kevlina.budgetplus.core.ui.TextField
import com.kevlina.budgetplus.core.ui.containerPadding

@Composable
internal fun SearchContent(
    modifier: Modifier = Modifier,
    state: SearchState,
) {
    Column(
        modifier = modifier
                .fillMaxSize()
                .containerPadding()
    ) {
        TextField(
            state = state.query,
            title = stringResource(R.string.search_field_title),
            placeholder = stringResource(R.string.search_field_placeholder),
            modifier = Modifier
                    .fillMaxWidth()
                    .padding(all = 16.dp)
        )

        SearchFilter(
            state = state.filter,
        )

        SearchResult(
            state = state.result
        )
    }
}

@Preview
@Composable
private fun SearchContent_Preview() = AppTheme(themeColors = ThemeColors.Lavender) {
    SearchContent(
        state = SearchState.preview,
        modifier = Modifier.background(LocalAppColors.current.light)
    )
}
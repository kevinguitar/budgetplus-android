package com.kevlina.budgetplus.feature.search.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.kevlina.budgetplus.core.theme.LocalAppColors
import com.kevlina.budgetplus.core.theme.ThemeColors
import com.kevlina.budgetplus.core.ui.AppTheme

@Composable
internal fun SearchFilter(
    modifier: Modifier = Modifier,
    state: SearchFilterState,
) {
    LazyRow(
        modifier = modifier.fillMaxWidth()
    ) {

    }
}

@Preview
@Composable
private fun SearchFilter_Preview() = AppTheme(ThemeColors.NemoSea) {
    SearchFilter(
        state = SearchFilterState.preview,
        modifier = Modifier.background(LocalAppColors.current.light)
    )
}
package com.kevlina.budgetplus.feature.search.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kevlina.budgetplus.core.common.R
import com.kevlina.budgetplus.core.common.RecordType
import com.kevlina.budgetplus.core.common.shortFormatted
import com.kevlina.budgetplus.core.theme.LocalAppColors
import com.kevlina.budgetplus.core.theme.ThemeColors
import com.kevlina.budgetplus.core.ui.AppTheme

@Composable
internal fun SearchFilter(
    modifier: Modifier = Modifier,
    state: SearchFilterState,
) {
    val type by state.type.collectAsStateWithLifecycle()
    val category by state.category.collectAsStateWithLifecycle()
    val period = state.period.collectAsStateWithLifecycle().value
    val author by state.author.collectAsStateWithLifecycle()
    val allAuthor by state.allAuthor.collectAsStateWithLifecycle()
    val showAuthorSelector = allAuthor.size > 1

    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
    ) {
        FilterPill(
            state = FilterPillState(
                placeholder = null,
                selection = stringResource(
                    when (type) {
                        RecordType.Expense -> R.string.record_expense
                        RecordType.Income -> R.string.record_income
                    }
                ),
                onClick = { TODO() }
            )
        )

        FilterPill(
            state = FilterPillState(
                placeholder = stringResource(R.string.search_all_categories),
                selection = category.name,
                onClick = { TODO() }
            )
        )

        FilterPill(
            state = FilterPillState(
                placeholder = null,
                selection = when (period) {
                    SearchPeriod.PastMonth -> stringResource(R.string.search_period_last_month)
                    SearchPeriod.PastHalfYear -> stringResource(R.string.search_period_last_half_year)
                    SearchPeriod.PastYear -> stringResource(R.string.search_period_last_year)
                    is SearchPeriod.Custom -> stringResource(
                        R.string.search_period_custom,
                        period.from.shortFormatted,
                        period.until.shortFormatted
                    )
                },
                onClick = { TODO() }
            )
        )

        if (showAuthorSelector) {
            FilterPill(
                state = FilterPillState(
                    placeholder = stringResource(R.string.overview_author_everyone),
                    selection = author?.name,
                    onClick = { TODO() }
                )
            )
        }
    }
}

@Preview
@Composable
private fun SearchFilter_Preview() = AppTheme(ThemeColors.Dusk) {
    SearchFilter(
        state = SearchFilterState.preview,
        modifier = Modifier
            .background(LocalAppColors.current.light)
            .padding(vertical = 16.dp)
    )
}
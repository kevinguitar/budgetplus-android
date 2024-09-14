package com.kevlina.budgetplus.feature.overview.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kevlina.budgetplus.core.common.EventFlow
import com.kevlina.budgetplus.core.common.MutableEventFlow
import com.kevlina.budgetplus.core.common.R
import com.kevlina.budgetplus.core.common.consumeEach
import com.kevlina.budgetplus.core.common.nav.AddDest
import com.kevlina.budgetplus.core.common.nav.Navigator
import com.kevlina.budgetplus.core.data.remote.TimePeriod
import com.kevlina.budgetplus.core.theme.LocalAppColors
import com.kevlina.budgetplus.core.ui.AppTheme
import com.kevlina.budgetplus.core.ui.DatePickerDialog
import com.kevlina.budgetplus.core.ui.Text
import com.kevlina.budgetplus.core.ui.rippleClick
import com.kevlina.budgetplus.feature.overview.OverviewTimeViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import java.time.LocalDate

@Composable
internal fun ColumnScope.TimePeriodSelector(
    uiState: TimePeriodSelectorUiState,
    navigator: Navigator,
) {

    val timePeriod by uiState.timePeriod.collectAsStateWithLifecycle()
    val fromDate by uiState.fromDate.collectAsStateWithLifecycle()
    val untilDate by uiState.untilDate.collectAsStateWithLifecycle()

    var showFromDatePicker by remember { mutableStateOf(false) }
    var showUntilDatePicker by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = uiState.openPremiumEvent) {
        uiState.openPremiumEvent
            .consumeEach { navigator.navigate(AddDest.UnlockPremium) }
            .collect()
    }

    DateRange(
        uiState = uiState,
        showFromDatePicker = { showFromDatePicker = true },
        showUntilDatePicker = { showUntilDatePicker = true }
    )

    TimePeriodPreset(
        timePeriod = timePeriod,
        setTimePeriod = uiState.setTimePeriod
    )

    if (showFromDatePicker) {
        DatePickerDialog(
            date = fromDate,
            onDismiss = { showFromDatePicker = false },
            onDatePicked = uiState.setFromDate
        )
    }

    if (showUntilDatePicker) {
        DatePickerDialog(
            date = untilDate,
            minDate = fromDate,
            onDismiss = { showUntilDatePicker = false },
            onDatePicked = uiState.setUntilDate
        )
    }
}

@Composable
fun TimePeriodPreset(
    timePeriod: TimePeriod,
    setTimePeriod: (TimePeriod) -> Unit,
) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.Top),
        modifier = Modifier.padding(vertical = 16.dp)
    ) {
        setOf(
            TimePeriod.Today,
            TimePeriod.Week,
            TimePeriod.Month,
            TimePeriod.LastMonth
        )
            .forEach { period ->
                TimePeriodPill(
                    timePeriod = period,
                    isSelected = timePeriod == period,
                    onClick = { setTimePeriod(period) }
                )
            }
    }
}

@Composable
private fun TimePeriodPill(
    timePeriod: TimePeriod,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .background(
                shape = AppTheme.cardShape,
                color = if (isSelected) {
                    LocalAppColors.current.dark
                } else {
                    LocalAppColors.current.primary
                }
            )
            .clip(AppTheme.cardShape)
            .rippleClick(onClick = onClick)
    ) {

        val titleRes = when (timePeriod) {
            is TimePeriod.Today -> R.string.overview_period_day
            is TimePeriod.Week -> R.string.overview_period_week
            is TimePeriod.Month -> R.string.overview_period_month
            is TimePeriod.LastMonth -> R.string.overview_period_last_month
            is TimePeriod.Custom -> error("Custom period doesn't shown in pill.")
        }

        Text(
            text = stringResource(id = titleRes),
            color = LocalAppColors.current.light,
            singleLine = true,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}

@Stable
internal class TimePeriodSelectorUiState(
    val timePeriod: StateFlow<TimePeriod>,
    val fromDate: StateFlow<LocalDate>,
    val untilDate: StateFlow<LocalDate>,
    val isOneDayPeriod: StateFlow<Boolean>,
    val openPremiumEvent: EventFlow<Unit>,
    val previousDay: () -> Unit,
    val nextDay: () -> Unit,
    val setTimePeriod: (TimePeriod) -> Unit,
    val setFromDate: (LocalDate) -> Unit,
    val setUntilDate: (LocalDate) -> Unit,
) {
    companion object {
        val preview = TimePeriodSelectorUiState(
            timePeriod = MutableStateFlow(TimePeriod.Month),
            fromDate = MutableStateFlow(TimePeriod.Month.from),
            untilDate = MutableStateFlow(TimePeriod.Month.until),
            isOneDayPeriod = MutableStateFlow(false),
            openPremiumEvent = MutableEventFlow(),
            previousDay = {},
            nextDay = {},
            setTimePeriod = {},
            setFromDate = {},
            setUntilDate = {}
        )
    }
}

internal fun OverviewTimeViewModel.toUiState() = TimePeriodSelectorUiState(
    timePeriod = timePeriod,
    fromDate = fromDate,
    untilDate = untilDate,
    isOneDayPeriod = isOneDayPeriod,
    openPremiumEvent = openPremiumEvent,
    previousDay = ::previousDay,
    nextDay = ::nextDay,
    setTimePeriod = ::setTimePeriod,
    setFromDate = ::setFromDate,
    setUntilDate = ::setUntilDate
)

@Preview
@Composable
private fun TimePeriodSelector_Preview() = AppTheme {
    Column(
        modifier = Modifier.background(LocalAppColors.current.lightBg),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TimePeriodSelector(
            uiState = TimePeriodSelectorUiState.preview,
            navigator = Navigator.empty
        )
    }
}
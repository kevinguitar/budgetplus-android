package com.kevlina.budgetplus.feature.overview.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.rounded.ChevronLeft
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.flowlayout.FlowMainAxisAlignment
import com.google.accompanist.flowlayout.FlowRow
import com.kevlina.budgetplus.core.common.R
import com.kevlina.budgetplus.core.common.consumeEach
import com.kevlina.budgetplus.core.common.mediumFormatted
import com.kevlina.budgetplus.core.common.nav.AddDest
import com.kevlina.budgetplus.core.common.nav.Navigator
import com.kevlina.budgetplus.core.data.remote.TimePeriod
import com.kevlina.budgetplus.core.ui.AppTheme
import com.kevlina.budgetplus.core.ui.DatePickerDialog
import com.kevlina.budgetplus.core.ui.Icon
import com.kevlina.budgetplus.core.ui.LocalAppColors
import com.kevlina.budgetplus.core.ui.Text
import com.kevlina.budgetplus.core.ui.rippleClick
import com.kevlina.budgetplus.feature.overview.OverviewTimeViewModel
import kotlinx.coroutines.flow.launchIn

@Composable
internal fun TimePeriodSelector(
    vm: OverviewTimeViewModel,
    navigator: Navigator,
) {

    val timePeriod by vm.timePeriod.collectAsStateWithLifecycle()
    val fromDate by vm.fromDate.collectAsStateWithLifecycle()
    val untilDate by vm.untilDate.collectAsStateWithLifecycle()
    val isOneDayPeriod by vm.isOneDayPeriod.collectAsStateWithLifecycle()

    var showFromDatePicker by remember { mutableStateOf(false) }
    var showUntilDatePicker by remember { mutableStateOf(false) }

    val arrowPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp)

    LaunchedEffect(key1 = vm) {
        vm.openPremiumEvent
            .consumeEach { navigator.navigate(AddDest.UnlockPremium.route) }
            .launchIn(this)
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally),
    ) {

        if (isOneDayPeriod) {
            Icon(
                imageVector = Icons.Rounded.ChevronLeft,
                tint = LocalAppColors.current.dark,
                modifier = Modifier
                    .padding(arrowPadding)
                    .rippleClick(borderless = true, onClick = vm::previousDay)
            )
        }

        Icon(
            imageVector = Icons.Filled.DateRange,
            contentDescription = stringResource(id = R.string.select_date),
            tint = LocalAppColors.current.dark
        )

        Text(
            text = fromDate.mediumFormatted,
            modifier = Modifier
                .rippleClick { showFromDatePicker = true }
                .padding(all = 8.dp)
        )

        if (isOneDayPeriod) {

            Icon(
                imageVector = Icons.Rounded.ChevronRight,
                tint = LocalAppColors.current.dark,
                modifier = Modifier
                    .padding(arrowPadding)
                    .rippleClick(borderless = true, onClick = vm::nextDay)
            )
        } else {

            Text(text = stringResource(id = R.string.date_to))

            Text(
                text = untilDate.mediumFormatted,
                modifier = Modifier
                    .rippleClick { showUntilDatePicker = true }
                    .padding(all = 8.dp)
            )
        }
    }

    FlowRow(
        mainAxisSpacing = 12.dp,
        crossAxisSpacing = 12.dp,
        mainAxisAlignment = FlowMainAxisAlignment.Center,
        modifier = Modifier.padding(top = 16.dp)
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
                    isSelected = timePeriod.from == period.from && timePeriod.until == period.until,
                    onClick = { vm.setTimePeriod(period) }
                )
            }
    }

    if (showFromDatePicker) {

        DatePickerDialog(
            date = fromDate,
            onDismiss = { showFromDatePicker = false },
            onDatePicked = vm::setFromDate
        )
    }

    if (showUntilDatePicker) {

        DatePickerDialog(
            date = untilDate,
            minDate = fromDate,
            onDismiss = { showUntilDatePicker = false },
            onDatePicked = vm::setUntilDate
        )
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
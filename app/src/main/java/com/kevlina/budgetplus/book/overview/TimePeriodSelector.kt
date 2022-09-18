package com.kevlina.budgetplus.book.overview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.flowlayout.FlowMainAxisAlignment
import com.google.accompanist.flowlayout.FlowRow
import com.kevlina.budgetplus.R
import com.kevlina.budgetplus.book.overview.vm.OverviewViewModel
import com.kevlina.budgetplus.data.remote.TimePeriod
import com.kevlina.budgetplus.ui.AppText
import com.kevlina.budgetplus.ui.AppTheme
import com.kevlina.budgetplus.ui.DatePickerDialog
import com.kevlina.budgetplus.ui.LocalAppColors
import com.kevlina.budgetplus.utils.rippleClick
import com.kevlina.budgetplus.utils.shortFormatted

@Composable
fun TimePeriodSelector() {

    val viewModel = hiltViewModel<OverviewViewModel>()

    val timePeriod by viewModel.timePeriod.collectAsState()
    val fromDate by viewModel.fromDate.collectAsState()
    val untilDate by viewModel.untilDate.collectAsState()

    var showFromDatePicker by remember { mutableStateOf(false) }
    var showUntilDatePicker by remember { mutableStateOf(false) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally),
    ) {

        Icon(
            imageVector = Icons.Filled.DateRange,
            contentDescription = stringResource(id = R.string.select_date),
            tint = LocalAppColors.current.dark
        )

        AppText(
            text = fromDate.shortFormatted,
            modifier = Modifier
                .rippleClick { showFromDatePicker = true }
                .padding(all = 8.dp)
        )

        AppText(text = stringResource(id = R.string.date_to))

        AppText(
            text = untilDate.shortFormatted,
            modifier = Modifier
                .rippleClick { showUntilDatePicker = true }
                .padding(all = 8.dp)
        )
    }

    FlowRow(
        mainAxisSpacing = 12.dp,
        crossAxisSpacing = 12.dp,
        mainAxisAlignment = FlowMainAxisAlignment.Center
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
                    onClick = { viewModel.setTimePeriod(period) }
                )
            }
    }

    if (showFromDatePicker) {

        DatePickerDialog(
            date = fromDate,
            onDismiss = { showFromDatePicker = false },
            onDatePicked = { from ->
                viewModel.setTimePeriod(
                    TimePeriod.Custom(
                        from = from,
                        until = if (from.isAfter(untilDate)) {
                            from
                        } else {
                            untilDate
                        }
                    )
                )
            }
        )
    }

    if (showUntilDatePicker) {

        DatePickerDialog(
            date = untilDate,
            minDate = fromDate,
            onDismiss = { showUntilDatePicker = false },
            onDatePicked = { until ->
                viewModel.setTimePeriod(TimePeriod.Custom(from = fromDate, until = until))
            }
        )
    }
}

@Composable
private fun TimePeriodPill(
    timePeriod: TimePeriod,
    isSelected: Boolean,
    onClick: () -> Unit
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

        AppText(
            text = stringResource(id = titleRes),
            color = LocalAppColors.current.light,
            singleLine = true,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}
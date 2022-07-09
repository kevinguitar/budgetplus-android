package com.kevingt.moneybook.book.overview

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.google.accompanist.flowlayout.FlowRow
import com.google.accompanist.flowlayout.SizeMode
import com.kevingt.moneybook.R
import com.kevingt.moneybook.book.overview.vm.OverviewViewModel
import com.kevingt.moneybook.book.overview.vm.TimePeriod
import com.kevingt.moneybook.ui.DatePickerDialog
import com.kevingt.moneybook.ui.DatePickerLabel
import com.kevingt.moneybook.ui.LocalAppColors

@Composable
fun TimePeriodSelector(viewModel: OverviewViewModel) {

    val timePeriod by viewModel.timePeriod.collectAsState()
    val fromDate by viewModel.fromDate.collectAsState()
    val untilDate by viewModel.untilDate.collectAsState()

    var showFromDatePicker by remember { mutableStateOf(false) }
    var showUntilDatePicker by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.padding(top = 16.dp)
    ) {
        DatePickerLabel(date = fromDate, onClick = { showFromDatePicker = true })
        DatePickerLabel(date = untilDate, onClick = { showUntilDatePicker = true })
    }

    FlowRow(
        mainAxisSize = SizeMode.Expand,
        mainAxisSpacing = 8.dp,
        modifier = Modifier.padding(16.dp)
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

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun TimePeriodPill(
    timePeriod: TimePeriod,
    isSelected: Boolean,
    onClick: () -> Unit
) {

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) {
            LocalAppColors.current.dark
        } else {
            LocalAppColors.current.primary
        },
        onClick = onClick
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
            textAlign = TextAlign.Center,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            color = LocalAppColors.current.light,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}
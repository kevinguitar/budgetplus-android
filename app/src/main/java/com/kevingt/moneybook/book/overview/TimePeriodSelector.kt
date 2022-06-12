package com.kevingt.moneybook.book.overview

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.google.accompanist.flowlayout.FlowRow
import com.google.accompanist.flowlayout.SizeMode
import com.kevingt.moneybook.book.overview.vm.OverviewViewModel
import com.kevingt.moneybook.book.overview.vm.TimePeriod
import com.kevingt.moneybook.ui.DatePickerDialog
import com.kevingt.moneybook.ui.DatePickerLabel

@Composable
fun TimePeriodSelector(viewModel: OverviewViewModel) {

    val timePeriod by viewModel.timePeriod.collectAsState()
    val fromDate by viewModel.fromDate.collectAsState()
    val untilDate by viewModel.untilDate.collectAsState()

    var showFromDatePicker by rememberSaveable { mutableStateOf(false) }
    var showUntilDatePicker by rememberSaveable { mutableStateOf(false) }

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
            maxDate = untilDate,
            onDismiss = { showFromDatePicker = false },
            onDatePicked = { from ->
                viewModel.setTimePeriod(
                    TimePeriod.Custom(
                        fromEpoch = from.toEpochDay(),
                        untilEpoch = untilDate.toEpochDay()
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
                viewModel.setTimePeriod(
                    TimePeriod.Custom(
                        fromEpoch = fromDate.toEpochDay(),
                        untilEpoch = until.toEpochDay()
                    )
                )
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

    Card(
        shape = RoundedCornerShape(16.dp),
        backgroundColor = if (isSelected) Color.Yellow else Color.White,
        onClick = onClick
    ) {

        val title = when (timePeriod) {
            is TimePeriod.Today -> "Today"
            is TimePeriod.Week -> "Week"
            is TimePeriod.Month -> "Month"
            is TimePeriod.LastMonth -> "Last Month"
            is TimePeriod.Custom -> error("Custom period doesn't shown in pill.")
        }

        Text(
            text = title,
            textAlign = TextAlign.Center,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}
package com.kevlina.budgetplus.core.ui

import android.widget.DatePicker
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import com.kevlina.budgetplus.core.common.R
import java.time.LocalDate
import java.util.concurrent.TimeUnit

@Composable
fun DatePickerDialog(
    date: LocalDate,
    minDate: LocalDate? = null,
    maxDate: LocalDate? = null,
    onDatePicked: (LocalDate) -> Unit,
    onDismiss: () -> Unit,
) {

    var currentDate by remember { mutableStateOf(date) }

    Dialog(onDismissRequest = onDismiss) {

        Column(
            modifier = Modifier
                .background(
                    color = Color.DarkGray,
                    shape = AppTheme.dialogShape
                )
                .clip(AppTheme.dialogShape)
        ) {

            CustomDatePicker(
                date = currentDate,
                minDate = minDate,
                maxDate = maxDate,
                onDateSelected = { currentDate = it }
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(bottom = 16.dp, end = 16.dp)
            ) {

                Text(
                    text = stringResource(id = R.string.cta_cancel),
                    color = Color.White,
                    modifier = Modifier
                        .clip(shape = RoundedCornerShape(8.dp))
                        .rippleClick(onClick = onDismiss)
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )

                Text(
                    text = stringResource(id = R.string.cta_confirm),
                    color = Color.White,
                    modifier = Modifier
                        .clip(shape = RoundedCornerShape(8.dp))
                        .rippleClick {
                            onDatePicked(currentDate)
                            onDismiss()
                        }
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun CustomDatePicker(
    date: LocalDate,
    minDate: LocalDate?,
    maxDate: LocalDate?,
    onDateSelected: (LocalDate) -> Unit,
) {
    AndroidView(
        modifier = Modifier.wrapContentSize(),
        factory = { context -> DatePicker(context) },
        update = { view ->
            view.init(
                /* year = */ date.year,
                /* monthOfYear = */ date.monthValue - 1,
                /* dayOfMonth = */ date.dayOfMonth
            ) { _, year, monthOfYear, dayOfMonth ->
                onDateSelected(
                    LocalDate.now()
                        .withMonth(monthOfYear + 1)
                        .withYear(year)
                        .withDayOfMonth(dayOfMonth)
                )
            }

            if (minDate != null) {
                view.minDate = minDate.toEpochDay() * TimeUnit.DAYS.toMillis(1)
            }

            if (maxDate != null) {
                view.maxDate = maxDate.toEpochDay() * TimeUnit.DAYS.toMillis(1)
            }
        }
    )
}

// Cannot use the new style because of this bug from desugaring library
// see: https://issuetracker.google.com/issues/300128109
/*
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kevlina.budgetplus.core.common.R
import com.kevlina.budgetplus.core.theme.LocalAppColors
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import androidx.compose.material3.DatePickerDialog as MaterialDatePickerDialog

@Composable
fun DatePickerDialog(
    date: LocalDate,
    minDate: LocalDate? = null,
    maxDate: LocalDate? = null,
    onDatePicked: (LocalDate) -> Unit,
    onDismiss: () -> Unit,
) {
    val dateState = rememberDatePickerState(
        initialSelectedDateMillis = date.utcMillis,
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return when {
                    minDate != null && maxDate != null -> {
                        utcTimeMillis >= minDate.utcMillis && utcTimeMillis <= maxDate.utcMillis
                    }

                    minDate != null -> utcTimeMillis >= minDate.utcMillis
                    maxDate != null -> utcTimeMillis <= maxDate.utcMillis
                    else -> true
                }
            }
        }
    )

    val colors = DatePickerDefaults.colors(
        containerColor = LocalAppColors.current.light,
        currentYearContentColor = LocalAppColors.current.dark,
        selectedYearContentColor = LocalAppColors.current.light,
        selectedYearContainerColor = LocalAppColors.current.dark,
        selectedDayContainerColor = LocalAppColors.current.dark,
        todayContentColor = LocalAppColors.current.dark,
        todayDateBorderColor = LocalAppColors.current.dark,
    )

    MaterialDatePickerDialog(
        onDismissRequest = onDismiss,
        colors = colors,
        dismissButton = {
            Text(
                text = stringResource(id = R.string.cta_cancel),
                color = LocalAppColors.current.dark,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .clip(shape = RoundedCornerShape(8.dp))
                    .rippleClick(onClick = onDismiss)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )
        },
        confirmButton = {
            Text(
                text = stringResource(id = R.string.cta_confirm),
                color = LocalAppColors.current.dark,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .padding(end = 16.dp)
                    .clip(shape = RoundedCornerShape(8.dp))
                    .rippleClick {
                        onDatePicked(dateState.selectedDateMillis?.utcLocaleDate ?: date)
                        onDismiss()
                    }
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
    ) {

        DatePicker(
            state = dateState,
            colors = colors,
            showModeToggle = false,
            title = {
                Text(
                    text = stringResource(id = R.string.select_date),
                    color = LocalAppColors.current.dark,
                    modifier = Modifier.padding(16.dp)
                )
            }
        )
    }
}

private val LocalDate.utcMillis
    get() = atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli()

private val Long.utcLocaleDate: LocalDate
    get() = Instant.ofEpochMilli(this).atOffset(ZoneOffset.UTC).toLocalDate()

@Preview
@Composable
private fun DatePickerDialog_Preview() = AppTheme {
    DatePickerDialog(
        date = LocalDate.now(),
        onDismiss = {},
        onDatePicked = {}
    )
}
*/
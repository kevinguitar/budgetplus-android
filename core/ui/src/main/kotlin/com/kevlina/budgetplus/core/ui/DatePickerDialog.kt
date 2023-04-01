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
                    text = stringResource(id = R.string.cta_ok),
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
// see: https://issuetracker.google.com/issues/160113376
/*
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kevlina.budgetplus.core.common.R
import com.kevlina.budgetplus.core.common.utcLocaleDate
import com.kevlina.budgetplus.core.common.utcMillis
import java.time.LocalDate
import androidx.compose.material3.DatePickerDialog as MaterialDatePickerDialog

@Composable
fun DatePickerDialog(
    date: LocalDate,
    minDate: LocalDate? = null,
    maxDate: LocalDate? = null,
    onDatePicked: (LocalDate) -> Unit,
    onDismiss: () -> Unit,
) {

    val dateState = rememberDatePickerState(initialSelectedDateMillis = date.utcMillis)
    val colors = DatePickerDefaults.colors(
        containerColor = LocalAppColors.current.light,
        currentYearContentColor = LocalAppColors.current.primarySemiDark,
        selectedYearContentColor = LocalAppColors.current.light,
        selectedYearContainerColor = LocalAppColors.current.primarySemiDark,
        selectedDayContainerColor = LocalAppColors.current.primarySemiDark,
        todayContentColor = LocalAppColors.current.primarySemiDark,
        todayDateBorderColor = LocalAppColors.current.primarySemiDark,
    )

    MaterialDatePickerDialog(
        onDismissRequest = onDismiss,
        colors = colors,
        dismissButton = {
            Text(
                text = stringResource(id = R.string.cta_cancel),
                color = Color.Black,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .clip(shape = RoundedCornerShape(8.dp))
                    .rippleClick(onClick = onDismiss)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )
        },
        confirmButton = {
            Text(
                text = stringResource(id = R.string.cta_ok),
                color = Color.Black,
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
                    color = Color.Black
                )
            },
            dateValidator = { millis ->
                when {
                    minDate != null && maxDate != null -> {
                        millis >= minDate.utcMillis && millis <= maxDate.utcMillis
                    }

                    minDate != null -> millis >= minDate.utcMillis
                    maxDate != null -> millis <= maxDate.utcMillis
                    else -> true
                }
            }
        )
    }
}*/

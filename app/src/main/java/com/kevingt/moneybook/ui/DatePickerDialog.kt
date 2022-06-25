package com.kevingt.moneybook.ui

import android.widget.DatePicker
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import com.kevingt.moneybook.R
import java.time.LocalDate
import java.util.concurrent.TimeUnit

@Composable
fun DatePickerDialog(
    date: LocalDate,
    minDate: LocalDate? = null,
    maxDate: LocalDate? = null,
    onDatePicked: (LocalDate) -> Unit,
    onDismiss: () -> Unit
) {

    var currentDate by remember { mutableStateOf(date) }

    Dialog(onDismissRequest = onDismiss) {

        Column(
            modifier = Modifier
                .wrapContentSize()
                .background(
                    color = Color.DarkGray,
                    shape = RoundedCornerShape(size = 16.dp)
                )
        ) {

            CustomDatePicker(
                date = currentDate,
                minDate = minDate,
                maxDate = maxDate,
                onDateSelected = { currentDate = it }
            )

            Row(
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(bottom = 16.dp, end = 16.dp)
            ) {
                TextButton(onClick = onDismiss) {
                    Text(
                        text = stringResource(id = R.string.cta_cancel),
                        style = MaterialTheme.typography.button,
                        color = MaterialTheme.colors.onPrimary
                    )
                }

                TextButton(
                    onClick = {
                        onDatePicked(currentDate)
                        onDismiss()
                    }
                ) {
                    Text(
                        text = stringResource(id = R.string.cta_confirm),
                        style = MaterialTheme.typography.button,
                        color = MaterialTheme.colors.onPrimary
                    )
                }

            }
        }
    }
}

@Composable
private fun CustomDatePicker(
    date: LocalDate,
    minDate: LocalDate?,
    maxDate: LocalDate?,
    onDateSelected: (LocalDate) -> Unit
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
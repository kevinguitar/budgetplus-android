package com.kevingt.moneybook.book.record

import android.widget.CalendarView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import com.kevingt.moneybook.utils.formatted
import java.time.LocalDate
import java.util.concurrent.TimeUnit

@Composable
fun DatePicker(
    onDatePicked: (LocalDate) -> Unit,
    onDismiss: () -> Unit
) {

    var currentDate by rememberSaveable { mutableStateOf(LocalDate.now()) }

    Dialog(onDismissRequest = onDismiss) {

        Column(
            modifier = Modifier
                .wrapContentSize()
                .background(
                    color = Color.DarkGray,
                    shape = RoundedCornerShape(size = 16.dp)
                )
        ) {

            Text(
                text = "Select Date",
                style = MaterialTheme.typography.h6,
                color = MaterialTheme.colors.onPrimary
            )

            Spacer(modifier = Modifier.size(16.dp))

            Text(
                text = currentDate.formatted,
                style = MaterialTheme.typography.h4,
                color = MaterialTheme.colors.onPrimary
            )

            CustomCalendarView(
                date = currentDate,
                onDateSelected = { currentDate = it }
            )

            Row(
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(bottom = 16.dp, end = 16.dp)
            ) {
                TextButton(onClick = onDismiss) {
                    Text(
                        text = "Cancel",
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
                        text = "OK",
                        style = MaterialTheme.typography.button,
                        color = MaterialTheme.colors.onPrimary
                    )
                }

            }
        }
    }
}

// https://stackoverflow.com/questions/60417233/jetpack-compose-date-time-picker
@Composable
fun CustomCalendarView(
    date: LocalDate,
    onDateSelected: (LocalDate) -> Unit
) {
    AndroidView(
        modifier = Modifier.wrapContentSize(),
        factory = { context -> CalendarView(context) },
        update = { view ->
            view.date = date.toEpochDay() * TimeUnit.DAYS.toMillis(1)
            view.setOnDateChangeListener { _, year, month, dayOfMonth ->
                onDateSelected(
                    LocalDate.now()
                        .withMonth(month + 1)
                        .withYear(year)
                        .withDayOfMonth(dayOfMonth)
                )
            }
        }
    )
}
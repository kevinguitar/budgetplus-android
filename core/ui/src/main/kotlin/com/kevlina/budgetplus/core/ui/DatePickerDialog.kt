package com.kevlina.budgetplus.core.ui

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

//TODO: LocalDate type is unstable
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
            AppText(
                text = stringResource(id = R.string.cta_cancel),
                color = Color.Black,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .clip(shape = RoundedCornerShape(8.dp))
                    .rippleClick(onClick = onDismiss)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )
        },
        confirmButton = {
            AppText(
                text = stringResource(id = R.string.cta_ok),
                color = Color.Black,
                fontWeight = FontWeight.SemiBold,
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
                AppText(
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
}
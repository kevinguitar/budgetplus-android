package com.kevlina.budgetplus.core.ui

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
import com.kevlina.budgetplus.core.theme.ThemeColors
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

    val lightColor = LocalAppColors.current.light
    val primaryColor = LocalAppColors.current.primary
    val darkColor = LocalAppColors.current.dark
    val colors = DatePickerDefaults.colors(
        containerColor = lightColor,
        titleContentColor = darkColor,
        headlineContentColor = darkColor,
        weekdayContentColor = darkColor,
        subheadContentColor = darkColor,
        navigationContentColor = darkColor,
        yearContentColor = darkColor,
        disabledYearContentColor = darkColor.copy(alpha = DisabledAlpha),
        currentYearContentColor = darkColor,
        selectedYearContentColor = lightColor,
        disabledSelectedYearContentColor = primaryColor.copy(alpha = DisabledAlpha),
        selectedYearContainerColor = primaryColor,
        disabledSelectedYearContainerColor = darkColor.copy(alpha = DisabledAlpha),
        dayContentColor = darkColor,
        disabledDayContentColor = darkColor.copy(alpha = DisabledAlpha),
        selectedDayContentColor = lightColor,
        disabledSelectedDayContentColor = lightColor.copy(alpha = DisabledAlpha),
        selectedDayContainerColor = primaryColor,
        disabledSelectedDayContainerColor = primaryColor.copy(alpha = DisabledAlpha),
        todayContentColor = darkColor,
        todayDateBorderColor = primaryColor,
        dayInSelectionRangeContentColor = darkColor,
        dayInSelectionRangeContainerColor = darkColor,
        dividerColor = darkColor,
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
                    .padding(all = 16.dp)
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
                    .padding(all = 16.dp)
            )
        }
    ) {
        DatePicker(
            state = dateState,
            colors = colors,
            showModeToggle = false
        )
    }
}

private const val DisabledAlpha = 0.38f

private val LocalDate.utcMillis
    get() = atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli()

private val Long.utcLocaleDate: LocalDate
    get() = Instant.ofEpochMilli(this).atOffset(ZoneOffset.UTC).toLocalDate()

@Preview
@Composable
private fun DatePickerDialog_Preview() = AppTheme(ThemeColors.Barbie) {
    DatePickerDialog(
        date = LocalDate.now(),
        minDate = LocalDate.now().minusWeeks(1),
        maxDate = LocalDate.now().plusWeeks(1),
        onDismiss = {},
        onDatePicked = {}
    )
}
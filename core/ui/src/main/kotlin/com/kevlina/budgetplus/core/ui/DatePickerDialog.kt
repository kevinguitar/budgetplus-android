package com.kevlina.budgetplus.core.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerColors
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

    val colors = datePickerColors()

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

@Composable
internal fun datePickerColors(): DatePickerColors {
    val lightColor = LocalAppColors.current.light
    val primaryColor = LocalAppColors.current.primary
    val darkColor = LocalAppColors.current.dark
    return DatePickerDefaults.colors(
        containerColor = lightColor,
        titleContentColor = darkColor,
        headlineContentColor = darkColor,
        weekdayContentColor = darkColor,
        subheadContentColor = darkColor,
        navigationContentColor = darkColor,
        yearContentColor = darkColor,
        disabledYearContentColor = darkColor.copy(alpha = DISABLED_ALPHA),
        currentYearContentColor = darkColor,
        selectedYearContentColor = lightColor,
        disabledSelectedYearContentColor = primaryColor.copy(alpha = DISABLED_ALPHA),
        selectedYearContainerColor = primaryColor,
        disabledSelectedYearContainerColor = darkColor.copy(alpha = DISABLED_ALPHA),
        dayContentColor = darkColor,
        disabledDayContentColor = darkColor.copy(alpha = DISABLED_ALPHA),
        selectedDayContentColor = lightColor,
        disabledSelectedDayContentColor = lightColor.copy(alpha = DISABLED_ALPHA),
        selectedDayContainerColor = primaryColor,
        disabledSelectedDayContainerColor = primaryColor.copy(alpha = DISABLED_ALPHA),
        todayContentColor = darkColor,
        todayDateBorderColor = primaryColor,
        dayInSelectionRangeContentColor = darkColor,
        dayInSelectionRangeContainerColor = primaryColor.copy(alpha = SELECTION_ALPHA),
        dividerColor = darkColor,
    )
}

private const val DISABLED_ALPHA = 0.38f
private const val SELECTION_ALPHA = 0.2f

internal val LocalDate.utcMillis
    get() = atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli()

internal val Long.utcLocaleDate: LocalDate
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
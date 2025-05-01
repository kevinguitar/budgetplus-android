package com.kevlina.budgetplus.core.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kevlina.budgetplus.core.common.R
import com.kevlina.budgetplus.core.theme.LocalAppColors
import com.kevlina.budgetplus.core.theme.ThemeColors
import java.time.LocalDate

@Composable
fun DateRangePickerDialog(
    startDate: LocalDate? = null,
    endDate: LocalDate? = null,
    minDate: LocalDate? = null,
    maxDate: LocalDate? = null,
    onRangePicked: (from: LocalDate, until: LocalDate) -> Unit,
    onDismiss: () -> Unit,
) {
    val dateRangeState = rememberDateRangePickerState(
        initialSelectedStartDateMillis = startDate?.utcMillis,
        initialSelectedEndDateMillis = endDate?.utcMillis,
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

    //TODO:
    // - configure transparent color for range
    // - fix picker width, it's too compact now
    AppDialog(
        onDismissRequest = onDismiss,
    ) {
        Column {
            DateRangePicker(
                state = dateRangeState,
                colors = datePickerColors(),
            )

            Row(modifier = Modifier.align(Alignment.End)) {
                Text(
                    text = stringResource(id = R.string.cta_cancel),
                    color = LocalAppColors.current.dark,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .clip(shape = RoundedCornerShape(8.dp))
                        .rippleClick(onClick = onDismiss)
                        .padding(all = 16.dp)
                )

                Text(
                    text = stringResource(id = R.string.cta_confirm),
                    color = LocalAppColors.current.dark,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .clip(shape = RoundedCornerShape(8.dp))
                        .rippleClick {
                            val startDate = dateRangeState.selectedStartDateMillis?.utcLocaleDate
                            val endDate = dateRangeState.selectedEndDateMillis?.utcLocaleDate
                            if (startDate != null && endDate != null) {
                                onRangePicked(startDate, endDate)
                            }
                            onDismiss()
                        }
                        .padding(all = 16.dp)
                )
            }
        }
    }
}

@Preview(widthDp = 400, heightDp = 600)
@Composable
private fun DateRangePickerDialog_Preview() = AppTheme(ThemeColors.Barbie) {
    DateRangePickerDialog(
        minDate = LocalDate.now().minusWeeks(1),
        maxDate = LocalDate.now().plusWeeks(1),
        onDismiss = {},
        onRangePicked = { _, _ -> },
    )
}
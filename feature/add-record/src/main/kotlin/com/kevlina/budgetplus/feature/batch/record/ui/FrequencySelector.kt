package com.kevlina.budgetplus.feature.batch.record.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kevlina.budgetplus.core.common.R
import com.kevlina.budgetplus.core.data.BatchFrequency
import com.kevlina.budgetplus.core.data.BatchUnit
import com.kevlina.budgetplus.core.theme.LocalAppColors
import com.kevlina.budgetplus.core.ui.AppTheme
import com.kevlina.budgetplus.core.ui.DropdownItem
import com.kevlina.budgetplus.core.ui.DropdownMenu
import com.kevlina.budgetplus.core.ui.Icon
import com.kevlina.budgetplus.core.ui.Text
import com.kevlina.budgetplus.core.ui.rippleClick

private const val DURATION_MIN = 1
private const val DURATION_MAX = 12

@Composable
internal fun FrequencySelector(
    frequency: BatchFrequency,
    setDuration: (Int) -> Unit,
    setUnit: (BatchUnit) -> Unit,
) {
    var isNumDropdownShown by remember { mutableStateOf(false) }
    var isUnitDropdownShown by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Rounded.Refresh,
            tint = LocalAppColors.current.dark,
            modifier = iconModifier
        )

        Spacer(Modifier.width(16.dp))

        Text(
            text = stringResource(id = R.string.batch_record_frequency),
            fontSize = fontSize,
            fontWeight = FontWeight.SemiBold,
        )

        Spacer(Modifier.width(16.dp))

        Text(
            text = stringResource(id = R.string.batch_record_every_duration),
            fontSize = fontSize,
        )

        Spacer(Modifier.width(12.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .rippleClick { isNumDropdownShown = true }
                .padding(vertical = 4.dp)
        ) {

            Text(
                text = frequency.duration.toString(),
                fontSize = fontSize,
            )

            Icon(
                imageVector = Icons.Rounded.ArrowDropDown,
                tint = LocalAppColors.current.dark
            )

            DropdownMenu(
                expanded = isNumDropdownShown,
                onDismissRequest = { isNumDropdownShown = false }
            ) {
                for (num in DURATION_MIN..DURATION_MAX) {
                    DropdownItem(
                        name = num.toString(),
                        onClick = {
                            setDuration(num)
                            isNumDropdownShown = false
                        }
                    )
                }
            }
        }

        Spacer(Modifier.width(4.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .rippleClick { isUnitDropdownShown = true }
                .padding(vertical = 4.dp)
        ) {

            Text(
                text = stringResource(id = frequency.unit.stringRes),
                fontSize = fontSize,
            )

            Icon(
                imageVector = Icons.Rounded.ArrowDropDown,
                tint = LocalAppColors.current.dark
            )

            DropdownMenu(
                expanded = isUnitDropdownShown,
                onDismissRequest = { isUnitDropdownShown = false }
            ) {
                BatchUnit.entries.forEach { unit ->
                    DropdownItem(
                        name = stringResource(id = unit.stringRes),
                        onClick = {
                            setUnit(unit)
                            isUnitDropdownShown = false
                        }
                    )
                }
            }
        }
    }
}

private val BatchUnit.stringRes: Int
    get() = when (this) {
        BatchUnit.Month -> R.string.batch_record_unit_month
        BatchUnit.Week -> R.string.batch_record_unit_week
        BatchUnit.Day -> R.string.batch_record_unit_day
    }

@Preview
@Composable
private fun FrequencySelector_Preview() = AppTheme {
    Box(
        modifier = Modifier
            .background(LocalAppColors.current.light)
            .padding(8.dp)
    ) {
        FrequencySelector(
            frequency = BatchFrequency(duration = 1, BatchUnit.Month),
            setDuration = {},
            setUnit = {},
        )
    }
}
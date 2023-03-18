package com.kevlina.budgetplus.feature.batch.record.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material.icons.rounded.Numbers
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Today
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kevlina.budgetplus.core.common.R
import com.kevlina.budgetplus.core.data.BatchFrequency
import com.kevlina.budgetplus.core.ui.DatePickerDialog
import com.kevlina.budgetplus.core.ui.DropdownItem
import com.kevlina.budgetplus.core.ui.DropdownMenu
import com.kevlina.budgetplus.core.ui.FontSize
import com.kevlina.budgetplus.core.ui.Icon
import com.kevlina.budgetplus.core.ui.LocalAppColors
import com.kevlina.budgetplus.core.ui.SingleDatePicker
import com.kevlina.budgetplus.core.ui.Text
import com.kevlina.budgetplus.core.ui.rippleClick
import com.kevlina.budgetplus.feature.batch.record.BatchRecordViewModel
import java.time.LocalDate

private val fontSize = FontSize.SemiLarge
private val iconModifier = Modifier
    .padding(top = 2.dp)
    .size(20.dp)

@Composable
internal fun BatchConfigSelector() {

    val vm = hiltViewModel<BatchRecordViewModel>()

    val date by vm.startDate.collectAsState()
    val frequency by vm.frequency.collectAsState()
    val times by vm.times.collectAsState()

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(LocalAppColors.current.lightBg)
            .padding(16.dp)
    ) {

        DateSelector(date = date, setStartDate = vm::setStartDate)

        FrequencySelector(frequency = frequency, setFrequency = vm::setFrequency)

        TimesSelector(times = times, batchTimes = vm.batchTimes, setTimes = vm::setTimes)
    }
}

@Composable
private fun DateSelector(
    date: LocalDate,
    setStartDate: (LocalDate) -> Unit,
) {
    var showDatePicker by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Icon(
            imageVector = Icons.Rounded.Today,
            contentDescription = stringResource(id = R.string.select_date),
            tint = LocalAppColors.current.dark,
            modifier = iconModifier
        )

        Text(
            text = stringResource(id = R.string.batch_record_start_date),
            fontSize = fontSize,
            fontWeight = FontWeight.SemiBold,
        )

        SingleDatePicker(
            date = date,
            showIcon = false,
            fontSize = fontSize,
            modifier = Modifier
                .rippleClick { showDatePicker = true }
                .padding(vertical = 4.dp)
        )
    }

    if (showDatePicker) {

        DatePickerDialog(
            date = date,
            onDatePicked = setStartDate,
            onDismiss = { showDatePicker = false }
        )
    }
}

@Composable
private fun FrequencySelector(
    frequency: BatchFrequency,
    setFrequency: (BatchFrequency) -> Unit,
) {
    var isFrequencyMenuShown by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Icon(
            imageVector = Icons.Rounded.Refresh,
            tint = LocalAppColors.current.dark,
            modifier = iconModifier
        )

        Text(
            text = stringResource(id = R.string.batch_record_frequency),
            fontSize = fontSize,
            fontWeight = FontWeight.SemiBold,
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .rippleClick { isFrequencyMenuShown = true }
                .padding(vertical = 4.dp)
        ) {

            Text(
                text = stringResource(id = frequency.stringRes),
                fontSize = fontSize,
            )

            Icon(
                imageVector = Icons.Rounded.ArrowDropDown,
                tint = LocalAppColors.current.dark
            )

            DropdownMenu(
                expanded = isFrequencyMenuShown,
                onDismissRequest = { isFrequencyMenuShown = false },
                modifier = Modifier.background(color = LocalAppColors.current.light)
            ) {

                BatchFrequency.values().forEach { freq ->
                    DropdownItem(
                        name = stringResource(id = freq.stringRes),
                        onClick = {
                            setFrequency(freq)
                            isFrequencyMenuShown = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun TimesSelector(
    times: Int,
    batchTimes: List<Int>,
    setTimes: (Int) -> Unit,
) {
    var isTimesMenuShown by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Icon(
            imageVector = Icons.Rounded.Numbers,
            tint = LocalAppColors.current.dark,
            modifier = iconModifier
        )

        Text(
            text = stringResource(id = R.string.batch_record_times),
            fontSize = fontSize,
            fontWeight = FontWeight.SemiBold,
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .rippleClick { isTimesMenuShown = true }
                .padding(vertical = 4.dp)
        ) {

            Text(
                text = times.toString(),
                fontSize = fontSize,
            )

            Icon(
                imageVector = Icons.Rounded.ArrowDropDown,
                tint = LocalAppColors.current.dark
            )

            DropdownMenu(
                expanded = isTimesMenuShown,
                onDismissRequest = { isTimesMenuShown = false },
                modifier = Modifier
                    .background(color = LocalAppColors.current.light)
                    .heightIn(max = 240.dp)
            ) {

                batchTimes.forEach { times ->
                    DropdownItem(
                        name = times.toString(),
                        onClick = {
                            setTimes(times)
                            isTimesMenuShown = false
                        }
                    )
                }
            }
        }
    }
}

private val BatchFrequency.stringRes: Int
    get() = when (this) {
        BatchFrequency.Monthly -> R.string.batch_record_frequency_monthly
        BatchFrequency.Weekly -> R.string.batch_record_frequency_weekly
        BatchFrequency.Daily -> R.string.batch_record_frequency_daily
    }
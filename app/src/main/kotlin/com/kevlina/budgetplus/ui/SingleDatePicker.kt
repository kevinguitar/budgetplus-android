package com.kevlina.budgetplus.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Today
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.kevlina.budgetplus.core.common.R
import com.kevlina.budgetplus.utils.shortFormatted
import java.time.LocalDate

@Composable
fun SingleDatePicker(
    date: LocalDate,
    modifier: Modifier,
) {

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
    ) {

        Icon(
            imageVector = Icons.Rounded.Today,
            contentDescription = stringResource(id = R.string.select_date),
            tint = LocalAppColors.current.dark
        )

        AppText(
            text = when {
                date.isEqual(LocalDate.now()) -> stringResource(id = R.string.today)
                date.plusDays(1).isEqual(LocalDate.now()) -> {
                    stringResource(id = R.string.yesterday)
                }

                date.minusDays(1).isEqual(LocalDate.now()) -> {
                    stringResource(id = R.string.tomorrow)
                }

                else -> date.shortFormatted
            }
        )
    }
}
package com.kevlina.budgetplus.core.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Today
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.kevlina.budgetplus.core.common.R
import com.kevlina.budgetplus.core.common.mediumFormatted
import com.kevlina.budgetplus.core.theme.LocalAppColors
import java.time.LocalDate

@Composable
fun SingleDatePicker(
    date: LocalDate,
    modifier: Modifier,
    showIcon: Boolean = true,
    fontSize: TextUnit = FontSize.Normal,
) {

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
    ) {

        if (showIcon) {
            Icon(
                imageVector = Icons.Rounded.Today,
                contentDescription = stringResource(id = R.string.select_date),
                tint = LocalAppColors.current.dark
            )
        }

        Text(
            text = when {
                date.isEqual(LocalDate.now()) -> stringResource(id = R.string.today)
                date.plusDays(1).isEqual(LocalDate.now()) -> {
                    stringResource(id = R.string.yesterday)
                }

                date.minusDays(1).isEqual(LocalDate.now()) -> {
                    stringResource(id = R.string.tomorrow)
                }

                else -> date.mediumFormatted
            },
            fontSize = fontSize
        )
    }
}
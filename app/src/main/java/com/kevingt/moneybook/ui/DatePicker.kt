package com.kevingt.moneybook.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.kevingt.moneybook.R
import com.kevingt.moneybook.utils.longFormatted
import java.time.LocalDate

@Composable
fun DatePickerLabel(
    date: LocalDate,
    onClick: () -> Unit
) {

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .clickable(onClick = onClick)
    ) {

        Icon(
            imageVector = Icons.Filled.DateRange,
            contentDescription = stringResource(id = R.string.select_date),
            tint = LocalAppColors.current.dark
        )

        Text(
            text = date.longFormatted,
            color = LocalAppColors.current.dark
        )
    }
}
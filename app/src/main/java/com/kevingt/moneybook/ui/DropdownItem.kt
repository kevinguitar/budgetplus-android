package com.kevingt.moneybook.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DropdownItem(
    name: String,
    onClick: () -> Unit
) {

    DropdownMenuItem(onClick = onClick) {
        Text(
            text = name,
            color = LocalAppColors.current.primarySemiDark
        )
    }
}

@Composable
fun DropdownMenuDivider() {

    Divider(modifier = Modifier.padding(vertical = 2.dp, horizontal = 12.dp))
}
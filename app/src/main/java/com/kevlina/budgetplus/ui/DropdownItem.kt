package com.kevlina.budgetplus.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.DropdownMenuItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun DropdownItem(
    name: String,
    icon: @Composable (() -> Unit)? = null,
    textColor: Color = LocalAppColors.current.primarySemiDark,
    onClick: () -> Unit
) {

    DropdownMenuItem(onClick = onClick) {

        if (icon != null) {
            icon()
        }

        AppText(
            text = name,
            color = textColor,
            fontSize = FontSize.SemiLarge
        )
    }
}

@Composable
fun DropdownMenuDivider() {

    Divider(modifier = Modifier.padding(vertical = 4.dp, horizontal = 12.dp))
}
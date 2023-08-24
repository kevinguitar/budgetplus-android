package com.kevlina.budgetplus.core.ui

import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.kevlina.budgetplus.core.theme.LocalAppColors
import androidx.compose.material3.Switch as MaterialSwitch

@Composable
fun Switch(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit),
    modifier: Modifier = Modifier,
) {

    MaterialSwitch(
        checked = checked,
        onCheckedChange = onCheckedChange,
        colors = SwitchDefaults.colors(
            checkedThumbColor = LocalAppColors.current.light,
            checkedTrackColor = LocalAppColors.current.dark,
            uncheckedThumbColor = LocalAppColors.current.dark,
            uncheckedTrackColor = LocalAppColors.current.light
        ),
        modifier = modifier
    )
}
package com.kevlina.budgetplus.feature.color.tone.picker.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
internal expect fun ColorPickerDialog(
    currentColor: Color,
    onColorPicked: (String) -> Unit,
    onDismiss: () -> Unit,
)
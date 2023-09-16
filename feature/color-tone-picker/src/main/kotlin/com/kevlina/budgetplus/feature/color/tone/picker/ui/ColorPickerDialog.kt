package com.kevlina.budgetplus.feature.color.tone.picker.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kevlina.budgetplus.core.common.R
import com.kevlina.budgetplus.core.theme.LocalAppColors
import com.kevlina.budgetplus.core.theme.convertHexToColor
import com.kevlina.budgetplus.core.ui.AppDialog
import com.kevlina.budgetplus.core.ui.AppTheme
import com.kevlina.budgetplus.core.ui.Button
import com.kevlina.budgetplus.core.ui.Text
import com.kevlina.budgetplus.core.ui.TextField
import com.skydoves.colorpickerview.ColorEnvelope
import com.skydoves.orchestra.colorpicker.BrightnessSlideBar
import com.skydoves.orchestra.colorpicker.ColorPicker
import timber.log.Timber

@Composable
internal fun ColorPickerDialog(
    currentColor: Color,
    onColorPicked: (String) -> Unit,
    onDismiss: () -> Unit,
) {

    var selectedColor by remember { mutableStateOf(ColorEnvelope(0)) }
    var hexCode by remember { mutableStateOf("") }
    var userInputColor by remember { mutableStateOf<Int?>(null) }

    AppDialog(
        onDismissRequest = onDismiss
    ) {

        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            ColorPicker(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                initialColor = currentColor,
                onColorListener = { envelope, _ ->
                    selectedColor = envelope
                    // Drop the FF which stands for alpha because it's always 1.
                    hexCode = envelope.hexCode.drop(2)
                },
                children = { colorPickerView ->
                    BrightnessSlideBar(
                        colorPickerView = colorPickerView,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )

                    LaunchedEffect(key1 = userInputColor) {
                        userInputColor?.let(colorPickerView::selectByHsvColor)
                    }
                }
            )

            TextField(
                value = hexCode,
                onValueChange = { newHexCode ->
                    // Skip the update if the change comes from the picker
                    if (newHexCode == selectedColor.hexCode.drop(2)) {
                        return@TextField
                    }
                    hexCode = newHexCode.trim()
                    try {
                        userInputColor = hexCode.convertHexToColor()
                    } catch (e: Exception) {
                        Timber.d(e, "Failed to parse color hex from user input. $newHexCode")
                    }
                },
                title = stringResource(id = R.string.color_tone_color_hex_code),
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                Button(onClick = onDismiss) {
                    Text(
                        text = stringResource(id = R.string.cta_cancel),
                        color = LocalAppColors.current.light,
                    )
                }

                Button(
                    onClick = { onColorPicked(selectedColor.hexCode) }
                ) {
                    Text(
                        text = stringResource(id = R.string.cta_confirm),
                        color = LocalAppColors.current.light,
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun ColorPickerDialog_Preview() = AppTheme {
    ColorPickerDialog(Color.Black, {}) {}
}
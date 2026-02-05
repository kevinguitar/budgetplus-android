package com.kevlina.budgetplus.feature.color.tone.picker.ui

import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import budgetplus.core.common.generated.resources.Res
import budgetplus.core.common.generated.resources.color_tone_color_hex_code
import budgetplus.core.common.generated.resources.cta_cancel
import budgetplus.core.common.generated.resources.cta_confirm
import co.touchlab.kermit.Logger
import com.kevlina.budgetplus.core.theme.LocalAppColors
import com.kevlina.budgetplus.core.theme.convertHexToColorInt
import com.kevlina.budgetplus.core.ui.AppDialog
import com.kevlina.budgetplus.core.ui.AppTheme
import com.kevlina.budgetplus.core.ui.Button
import com.kevlina.budgetplus.core.ui.Text
import com.kevlina.budgetplus.core.ui.TextField
import com.kevlina.budgetplus.feature.color.tone.picker.R
import com.skydoves.colorpickerview.ColorEnvelope
import com.skydoves.orchestra.colorpicker.BrightnessSlideBar
import com.skydoves.orchestra.colorpicker.ColorPicker
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun ColorPickerDialog(
    currentColor: Color,
    onColorPicked: (String) -> Unit,
    onDismiss: () -> Unit,
) {

    var selectedColor by remember { mutableStateOf(ColorEnvelope(0)) }
    val hexCode = rememberTextFieldState("")
    var userInputColor by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(hexCode.text) {
        val newHexCode = hexCode.text
        // Skip the update if the change comes from the picker
        if (newHexCode == selectedColor.hexCode.drop(2)) {
            return@LaunchedEffect
        }
        hexCode.setTextAndPlaceCursorAtEnd(hexCode.text.toString().trim())
        try {
            userInputColor = hexCode.text.convertHexToColorInt()
        } catch (e: Exception) {
            Logger.d(e) { "Failed to parse color hex from user input. $newHexCode" }
        }
    }

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
                    hexCode.setTextAndPlaceCursorAtEnd(envelope.hexCode.drop(2))
                },
                children = { colorPickerView ->
                    BrightnessSlideBar(
                        colorPickerView = colorPickerView,
                        selector = R.drawable.ic_color_selector,
                        modifier = Modifier
                            .padding(top = 16.dp)
                            .fillMaxWidth()
                            .height(24.dp)
                            .clip(RoundedCornerShape(AppTheme.cornerRadius))
                    )

                    LaunchedEffect(key1 = userInputColor) {
                        userInputColor?.let(colorPickerView::selectByHsvColor)
                    }
                }
            ) {
                //TODO: Migrate to compose resources
                //TODO: Actually let's wait until migrating to color-picker-compose
                val selector = AppCompatResources.getDrawable(it.context, R.drawable.ic_color_selector)
                it.setSelectorDrawable(selector)
            }

            TextField(
                state = hexCode,
                title = stringResource(Res.string.color_tone_color_hex_code),
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                Button(onClick = onDismiss) {
                    Text(
                        text = stringResource(Res.string.cta_cancel),
                        color = LocalAppColors.current.light,
                    )
                }

                Button(
                    onClick = { onColorPicked(selectedColor.hexCode) }
                ) {
                    Text(
                        text = stringResource(Res.string.cta_confirm),
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
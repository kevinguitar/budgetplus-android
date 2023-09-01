package com.kevlina.budgetplus.core.lottie

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.airbnb.lottie.LottieProperty
import com.airbnb.lottie.compose.rememberLottieDynamicProperty

@Composable
fun rememberColorProperty(
    color: Color,
    vararg keyPath: String,
) = rememberLottieDynamicProperty(
    property = LottieProperty.COLOR,
    value = color.toArgb(),
    keyPath = keyPath
)

@Composable
fun rememberStrokeColorProperty(
    color: Color,
    vararg keyPath: String,
) = rememberLottieDynamicProperty(
    property = LottieProperty.STROKE_COLOR,
    value = color.toArgb(),
    keyPath = keyPath
)
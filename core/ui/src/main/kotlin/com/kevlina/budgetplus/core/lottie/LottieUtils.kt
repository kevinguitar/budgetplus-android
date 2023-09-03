package com.kevlina.budgetplus.core.lottie

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.airbnb.lottie.LottieProperty
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.airbnb.lottie.compose.rememberLottieDynamicProperty
import com.kevlina.budgetplus.core.common.R

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

@Composable
fun PremiumCrown(modifier: Modifier = Modifier) {
    val icPremium by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.ic_premium))
    LottieAnimation(
        composition = icPremium,
        iterations = LottieConstants.IterateForever,
        modifier = modifier
    )
}
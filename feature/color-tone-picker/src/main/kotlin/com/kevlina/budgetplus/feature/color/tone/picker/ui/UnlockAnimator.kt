package com.kevlina.budgetplus.feature.color.tone.picker.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.airbnb.lottie.compose.rememberLottieDynamicProperties
import com.kevlina.budgetplus.core.common.R
import com.kevlina.budgetplus.core.lottie.rememberColorProperty
import com.kevlina.budgetplus.core.lottie.rememberStrokeColorProperty
import com.kevlina.budgetplus.core.theme.LocalAppColors
import com.kevlina.budgetplus.core.ui.AppTheme

@Composable
internal fun UnlockAnimator(
    color: Color,
    modifier: Modifier = Modifier,
) {

    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.img_unlock))
    val dynamicProperties = rememberLottieDynamicProperties(
        rememberStrokeColorProperty(color = color, "shackle", "Group 2", "Stroke 1"),
        rememberColorProperty(color = color, "body", "Group 1", "Fill 1"),
    )

    LottieAnimation(
        composition = composition,
        iterations = LottieConstants.IterateForever,
        dynamicProperties = dynamicProperties,
        modifier = modifier
    )
}

@Preview
@Composable
private fun UnlockAnimator_Preview() = AppTheme {
    UnlockAnimator(color = LocalAppColors.current.dark)
}
package com.kevlina.budgetplus.feature.overview.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.airbnb.lottie.compose.rememberLottieDynamicProperties
import com.kevlina.budgetplus.core.common.R
import com.kevlina.budgetplus.core.lottie.rememberColorProperty
import com.kevlina.budgetplus.core.lottie.rememberStrokeColorProperty
import com.kevlina.budgetplus.core.theme.LocalAppColors
import com.kevlina.budgetplus.core.ui.blend

@Composable
fun OverviewZeroCase() {

    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.img_empty))

    val bgColor = LocalAppColors.current.lightBg
    val bgDarkenColor = bgColor.blend(LocalAppColors.current.light, 0.5F)
    val strokeColor = LocalAppColors.current.dark
    val dynamicProperties = rememberLottieDynamicProperties(
        // The X icon
        rememberColorProperty(color = strokeColor, "LUPA rotacion 3D", "Group 1", "Fill 1"),
        // The outline of the magnifier
        rememberStrokeColorProperty(color = strokeColor, "LUPA rotacion 3D", "Group 2", "Stroke 1"),
        // The grip of the magnifier
        rememberColorProperty(color = strokeColor, "LUPA rotacion 3D", "Group 3", "Fill 1"),
        // The lines in the document
        rememberStrokeColorProperty(color = strokeColor, "**", "Group 1", "Stroke 1"),
        // Outlines of the document
        rememberColorProperty(color = strokeColor, "papel bot Outlines", "Group 1", "Fill 1"),
        rememberColorProperty(color = strokeColor, "Papel front Outlines", "Group 1", "Fill 1"),
        rememberStrokeColorProperty(color = strokeColor, "Papel front Outlines", "Group 2", "Stroke 1"),
        rememberColorProperty(color = strokeColor, "Papel top Outlines", "Group 1", "Fill 1"),
        rememberColorProperty(color = strokeColor, "Papel top Outlines", "Group 2", "Fill 1"),
        // Top-left gradient of the document
        rememberColorProperty(color = bgDarkenColor, "Papel front Outlines", "Group 3", "Fill 1"),
        // The little circle dot on the right
        rememberColorProperty(color = strokeColor, "circulito Outlines", "Group 1", "Fill 1"),
        // The little x on the left
        rememberColorProperty(color = strokeColor, "x 2 Outlines", "Group 1", "Fill 1"),
        // The big area of the background
        rememberColorProperty(color = bgColor, "bg Outlines", "Group 1", "Fill 1")
    )

    LottieAnimation(
        composition = composition,
        speed = 1.5F,
        dynamicProperties = dynamicProperties,
        modifier = Modifier
            .padding(top = 32.dp)
            .size(240.dp)
    )
}
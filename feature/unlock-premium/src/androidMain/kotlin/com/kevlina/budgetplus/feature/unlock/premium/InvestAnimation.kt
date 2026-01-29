package com.kevlina.budgetplus.feature.unlock.premium

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.airbnb.lottie.compose.rememberLottieDynamicProperties
import com.kevlina.budgetplus.core.common.R
import com.kevlina.budgetplus.core.lottie.rememberColorProperty
import com.kevlina.budgetplus.core.theme.LocalAppColors
import com.kevlina.budgetplus.core.theme.ThemeColors
import com.kevlina.budgetplus.core.ui.AppTheme

@Composable
internal fun InvestAnimation(
    modifier: Modifier = Modifier,
) {

    val imgInvest by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.img_invest))

    val primaryColor = LocalAppColors.current.primary
    val darkColor = LocalAppColors.current.dark
    val dynamicProperties = rememberLottieDynamicProperties(
        // The girl's skirt
        rememberColorProperty(color = primaryColor, "Isolation Mode 15", "Group 3", "Fill 1"),
        // The man's pant
        rememberColorProperty(color = darkColor, "Isolation Mode 11", "Group 1", "Group 3", "Fill 1"),
        rememberColorProperty(color = darkColor, "Isolation Mode 10", "Group 5", "Group 2", "Fill 1"),
        // The man's t-shirt
        rememberColorProperty(color = primaryColor, "Isolation Mode 5", "Group 7", "Fill 1"),
        // The leaves
        rememberColorProperty(color = darkColor, "Isolation Mode 3", "Group 4", "Fill 1"),
        rememberColorProperty(color = darkColor, "Isolation Mode 2", "Group 6", "Fill 1"),
    )

    LottieAnimation(
        composition = imgInvest,
        iterations = LottieConstants.IterateForever,
        contentScale = ContentScale.FillWidth,
        dynamicProperties = dynamicProperties,
        modifier = modifier
    )
}

@Preview
@Composable
private fun InvestAnimation_Preview() = AppTheme(themeColors = ThemeColors.Barbie) {
    InvestAnimation()
}
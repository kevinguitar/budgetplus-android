package com.kevlina.budgetplus.feature.unlock.premium

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import com.kevlina.budgetplus.core.lottie.loadLottieSpec
import com.kevlina.budgetplus.core.theme.LocalAppColors
import com.kevlina.budgetplus.core.theme.ThemeColors
import com.kevlina.budgetplus.core.ui.AppTheme
import io.github.alexzhirkevich.compottie.Compottie
import io.github.alexzhirkevich.compottie.ExperimentalCompottieApi
import io.github.alexzhirkevich.compottie.dynamic.rememberLottieDynamicProperties
import io.github.alexzhirkevich.compottie.rememberLottieComposition
import io.github.alexzhirkevich.compottie.rememberLottiePainter

@Composable
internal fun InvestAnimation(
    modifier: Modifier = Modifier,
) {
    val imgInvest by rememberLottieComposition { loadLottieSpec("img_invest") }

    val primaryColor = LocalAppColors.current.primary
    val darkColor = LocalAppColors.current.dark

    @OptIn(ExperimentalCompottieApi::class)
    val dynamicProperties = rememberLottieDynamicProperties {
        // The girl's skirt
        shapeLayer("Isolation Mode 15") {
            fill("Group 3", "Fill 1") {
                color { primaryColor }
            }
        }
        // The man's pant
        shapeLayer("Isolation Mode 11") {
            fill("Group 3", "Fill 1") {
                color { darkColor }
            }
        }
        shapeLayer("Isolation Mode 10") {
            fill("Group 2", "Fill 1") {
                color { darkColor }
            }
        }
        // The man's t-shirt
        shapeLayer("Isolation Mode 5") {
            fill("Group 7", "Fill 1") {
                color { primaryColor }
            }
        }
        // The leaves
        shapeLayer("Isolation Mode 3") {
            fill("Group 4", "Fill 1") {
                color { darkColor }
            }
        }
        shapeLayer("Isolation Mode 2") {
            fill("Group 6", "Fill 1") {
                color { darkColor }
            }
        }
    }

    Image(
        painter = rememberLottiePainter(
            composition = imgInvest,
            iterations = Compottie.IterateForever,
            dynamicProperties = dynamicProperties
        ),
        contentDescription = null,
        contentScale = ContentScale.FillWidth,
        modifier = modifier
    )
}

@Preview
@Composable
private fun InvestAnimation_Preview() = AppTheme(themeColors = ThemeColors.Barbie) {
    InvestAnimation()
}
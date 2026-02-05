package com.kevlina.budgetplus.feature.color.tone.picker.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kevlina.budgetplus.core.lottie.loadLottieSpec
import com.kevlina.budgetplus.core.theme.LocalAppColors
import com.kevlina.budgetplus.core.ui.AppTheme
import io.github.alexzhirkevich.compottie.Compottie
import io.github.alexzhirkevich.compottie.ExperimentalCompottieApi
import io.github.alexzhirkevich.compottie.dynamic.rememberLottieDynamicProperties
import io.github.alexzhirkevich.compottie.rememberLottieComposition
import io.github.alexzhirkevich.compottie.rememberLottiePainter

@Composable
internal fun UnlockAnimator(
    color: Color,
    modifier: Modifier = Modifier,
) {

    val composition by rememberLottieComposition { loadLottieSpec("img_unlock") }
    @OptIn(ExperimentalCompottieApi::class)
    val dynamicProperties = rememberLottieDynamicProperties {
        shapeLayer("shackle") {
            stroke("Group 2", "Stroke 1") {
                color { color }
            }
        }
        shapeLayer("body") {
            fill("Group 1", "Fill 1") {
                color { color }
            }
        }
    }

    Image(
        painter = rememberLottiePainter(
            composition = composition,
            iterations = Compottie.IterateForever,
            dynamicProperties = dynamicProperties,
        ),
        contentDescription = null,
        modifier = modifier.size(240.dp)
    )
}

@Preview
@Composable
private fun UnlockAnimator_Preview() = AppTheme {
    UnlockAnimator(color = LocalAppColors.current.dark)
}
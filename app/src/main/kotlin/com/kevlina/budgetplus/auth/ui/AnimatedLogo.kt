package com.kevlina.budgetplus.auth.ui

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kevlina.budgetplus.R

@Composable
fun AnimatedLogo() {

    val transition = rememberInfiniteTransition()

    val bgRotation by transition.animateFloat(
        initialValue = 0F,
        targetValue = 359F,
        animationSpec = infiniteRepeatable(
            animation = tween(10_000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    val logoTranslationYTotal = 10F
    val logoTranslationY by transition.animateFloat(
        initialValue = 0F,
        targetValue = logoTranslationYTotal,
        animationSpec = infiniteRepeatable(
            animation = tween(1_000),
            repeatMode = RepeatMode.Reverse
        )
    )

    Box(contentAlignment = Alignment.Center) {

        Image(
            painter = painterResource(id = R.drawable.ic_logo_bg),
            contentDescription = null,
            modifier = Modifier
                .size(240.dp)
                .graphicsLayer { rotationZ = bgRotation }
        )

        Image(
            painter = painterResource(id = R.drawable.ic_logo),
            contentDescription = null,
            modifier = Modifier
                .padding(start = 12.dp)
                .size(160.dp)
                .graphicsLayer {
                    translationY = logoTranslationY - logoTranslationYTotal / 2
                }
        )
    }
}

@Preview
@Composable
private fun AnimatedLogo_Preview() = AnimatedLogo()
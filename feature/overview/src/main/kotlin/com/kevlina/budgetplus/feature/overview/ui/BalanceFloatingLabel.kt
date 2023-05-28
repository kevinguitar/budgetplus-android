package com.kevlina.budgetplus.feature.overview.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kevlina.budgetplus.core.common.R
import com.kevlina.budgetplus.core.common.dollar
import com.kevlina.budgetplus.core.ui.FontSize
import com.kevlina.budgetplus.core.ui.LocalAppColors
import com.kevlina.budgetplus.core.ui.Text
import com.kevlina.budgetplus.core.ui.blockClicks
import kotlinx.coroutines.delay

private const val LABEL_SCALE_ZOOM = 1.5F
private const val LABEL_SCALE_NORMAL = 1F
private const val LABEL_ZOOM_DURATION = 1000L
private const val LABEL_TRANSLATION_Y = -120

@Composable
fun BalanceFloatingLabel(
    balance: Double,
    modifier: Modifier = Modifier,
) {

    var zoomIn by rememberSaveable(balance) { mutableStateOf(true) }
    val scale by animateFloatAsState(
        targetValue = if (zoomIn) LABEL_SCALE_ZOOM else LABEL_SCALE_NORMAL,
        label = "BalanceFloatingLabel"
    )

    LaunchedEffect(key1 = balance) {
        delay(LABEL_ZOOM_DURATION)
        zoomIn = false
    }

    Box(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                translationY = LABEL_TRANSLATION_Y * (scale - 1F)
            }
            .clip(CircleShape)
            .background(LocalAppColors.current.dark)
            .blockClicks()
    ) {

        Text(
            text = stringResource(id = R.string.overview_balance, balance.dollar),
            color = LocalAppColors.current.light,
            fontWeight = FontWeight.Medium,
            fontSize = FontSize.SemiLarge,
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp)
        )
    }
}
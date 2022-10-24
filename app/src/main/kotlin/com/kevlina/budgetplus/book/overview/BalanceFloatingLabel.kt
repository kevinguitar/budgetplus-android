package com.kevlina.budgetplus.book.overview

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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kevlina.budgetplus.core.common.R
import com.kevlina.budgetplus.ui.AppText
import com.kevlina.budgetplus.ui.FontSize
import com.kevlina.budgetplus.ui.LocalAppColors
import com.kevlina.budgetplus.utils.dollar
import kotlinx.coroutines.delay

@Composable
fun BalanceFloatingLabel(
    balance: Double,
    modifier: Modifier = Modifier,
) {

    var zoomIn by rememberSaveable(balance) { mutableStateOf(true) }
    val scale by animateFloatAsState(targetValue = if (zoomIn) 1.5F else 1F)

    LaunchedEffect(key1 = balance) {
        delay(1000)
        zoomIn = false
    }

    Box(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                translationY = -120 * (scale - 1F)
            }
            .background(
                color = LocalAppColors.current.dark,
                shape = CircleShape
            )
    ) {

        AppText(
            text = stringResource(id = R.string.overview_balance, balance.dollar),
            color = LocalAppColors.current.light,
            fontWeight = FontWeight.Medium,
            fontSize = FontSize.SemiLarge,
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp)
        )
    }
}
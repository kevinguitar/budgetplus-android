package com.kevlina.budgetplus.feature.add.record.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieAnimatable
import com.airbnb.lottie.compose.rememberLottieComposition
import com.airbnb.lottie.compose.rememberLottieDynamicProperties
import com.kevlina.budgetplus.core.common.EventTrigger
import com.kevlina.budgetplus.core.common.R
import com.kevlina.budgetplus.core.common.consumeEach
import com.kevlina.budgetplus.core.lottie.rememberColorProperty
import com.kevlina.budgetplus.core.lottie.rememberStrokeColorProperty
import com.kevlina.budgetplus.core.theme.LocalAppColors
import com.kevlina.budgetplus.core.theme.ThemeColors
import com.kevlina.budgetplus.core.ui.AppTheme
import com.kevlina.budgetplus.core.ui.rememberSafeFocusManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlin.time.Duration.Companion.seconds

@Composable
fun BoxScope.DoneAnimator(eventTrigger: EventTrigger<Unit>) {

    val focusManager = rememberSafeFocusManager()

    var showAnimation by remember { mutableStateOf(false) }
    val imgDone by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.img_done))
    val lottieAnimatable = rememberLottieAnimatable()

    val bgColor = LocalAppColors.current.dark
    val strokeColor = LocalAppColors.current.light
    val dynamicProperties = rememberLottieDynamicProperties(
        // The big circle outline
        rememberStrokeColorProperty(color = bgColor, "Rectangle 6 Copy", "Rectangle 6 Copy", "Stroke 1"),
        // The big circle filled color
        rememberColorProperty(color = bgColor, "Rectangle 6 Copy", "Rectangle 6 Copy", "Fill 1"),
        // The check mark stroke
        rememberStrokeColorProperty(color = strokeColor, "Path 2", "Path 2", "Stroke 1"),
    )

    LaunchedEffect(key1 = eventTrigger) {
        eventTrigger.event.consumeEach {
            focusManager.clearFocus()

            showAnimation = true
            lottieAnimatable.animate(
                composition = imgDone,
                speed = 1.2F
            )
            showAnimation = false
        }.collect()
    }

    if (showAnimation) {
        LottieAnimation(
            composition = imgDone,
            progress = { lottieAnimatable.progress },
            dynamicProperties = dynamicProperties,
            modifier = Modifier
                .size(160.dp)
                .align(Alignment.Center),
        )
    }
}

@Preview
@Composable
private fun DoneAnimator_Preview() = AppTheme(themeColors = ThemeColors.Lavender) {
    val eventTrigger = remember { EventTrigger<Unit>() }
    Box {
        DoneAnimator(eventTrigger = eventTrigger)
    }
    LaunchedEffect(key1 = Unit) {
        while (true) {
            delay(1.seconds)
            eventTrigger.sendEvent(Unit)
        }
    }
}
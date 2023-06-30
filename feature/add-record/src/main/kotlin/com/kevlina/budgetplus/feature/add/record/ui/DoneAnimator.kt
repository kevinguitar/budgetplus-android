package com.kevlina.budgetplus.feature.add.record.ui

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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieAnimatable
import com.airbnb.lottie.compose.rememberLottieComposition
import com.kevlina.budgetplus.core.common.EventFlow
import com.kevlina.budgetplus.core.common.R
import com.kevlina.budgetplus.core.common.consumeEach
import kotlinx.coroutines.flow.collect

@Composable
fun BoxScope.DoneAnimator(event: EventFlow<Unit>) {

    val focusManager = LocalFocusManager.current

    var showAnimation by remember { mutableStateOf(false) }
    val imgDone by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.img_done))
    val lottieAnimatable = rememberLottieAnimatable()

    LaunchedEffect(key1 = event) {
        event.consumeEach {
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
            modifier = Modifier
                .size(160.dp)
                .align(Alignment.Center),
        )
    }
}
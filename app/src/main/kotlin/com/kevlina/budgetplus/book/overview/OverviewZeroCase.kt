package com.kevlina.budgetplus.book.overview

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.kevlina.budgetplus.R

@Composable
fun OverviewZeroCase() {

    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.img_empty))

    LottieAnimation(
        composition = composition,
        speed = 1.5F,
        modifier = Modifier
            .padding(top = 32.dp)
            .size(240.dp)
    )
}
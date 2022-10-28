package com.kevlina.budgetplus.feature.welcome.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.kevlina.budgetplus.core.common.R
import com.kevlina.budgetplus.core.ui.AppText
import com.kevlina.budgetplus.core.ui.AppTheme
import com.kevlina.budgetplus.core.ui.FontSize
import com.kevlina.budgetplus.core.ui.LocalAppColors

@Composable
fun CollabBlock(modifier: Modifier) {

    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.img_collab))

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.background(LocalAppColors.current.primary)
    ) {

        AppText(
            text = stringResource(id = R.string.welcome_join_instruction),
            color = LocalAppColors.current.light,
            fontSize = FontSize.SemiLarge,
            lineHeight = 24.sp,
            modifier = Modifier
                .weight(1F)
                .width(AppTheme.maxContentWidth)
                .padding(24.dp)
        )

        LottieAnimation(
            composition = composition,
            modifier = Modifier.height(200.dp)
        )
    }
}
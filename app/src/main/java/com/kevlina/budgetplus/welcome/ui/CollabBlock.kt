package com.kevlina.budgetplus.welcome.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import com.kevlina.budgetplus.R
import com.kevlina.budgetplus.ui.AppText
import com.kevlina.budgetplus.ui.FontSize
import com.kevlina.budgetplus.ui.LocalAppColors

@Composable
fun ColumnScope.CollabBlock() {

    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.img_collab))

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .weight(1F)
            .background(LocalAppColors.current.primary)
    ) {

        AppText(
            text = stringResource(id = R.string.welcome_join_instruction),
            color = LocalAppColors.current.light,
            fontSize = FontSize.SemiLarge,
            lineHeight = 24.sp,
            modifier = Modifier
                .weight(1F)
                .padding(24.dp)
        )

        LottieAnimation(
            composition = composition,
            modifier = Modifier
                .align(alignment = Alignment.CenterHorizontally)
                .height(200.dp)
        )
    }
}
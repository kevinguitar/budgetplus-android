package com.kevingt.moneybook.book.record

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.kevingt.moneybook.R
import com.kevingt.moneybook.book.record.vm.RecordViewModel
import com.kevingt.moneybook.ui.AppButton
import com.kevingt.moneybook.ui.AppText
import com.kevingt.moneybook.ui.FontSize
import com.kevingt.moneybook.ui.LocalAppColors

@Composable
fun ColumnScope.RecordButton(viewModel: RecordViewModel) {

    var showCheckAnimation by remember { mutableStateOf(false) }
    val checkAnimation by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.ic_check))
    val checkState = animateLottieCompositionAsState(
        composition = checkAnimation,
        speed = 1.2F,
        isPlaying = showCheckAnimation
    )

    if (checkState.isPlaying && checkState.isAtEnd) {
        showCheckAnimation = false
    }

    val focusManager = LocalFocusManager.current
    val context = LocalContext.current

    AppButton(
        modifier = Modifier
            .align(Alignment.CenterHorizontally)
            .size(width = 120.dp, height = 48.dp),
        shape = CircleShape,
        onClick = {
            if (viewModel.record()) {
                focusManager.clearFocus()
                showCheckAnimation = true
                viewModel.showFullScreenAdIfNeeded(context)
            }
        },
    ) {

        if (showCheckAnimation) {

            LottieAnimation(
                composition = checkAnimation,
                progress = { checkState.progress },
                modifier = Modifier.size(48.dp),
            )
        } else {

            AppText(
                text = stringResource(id = R.string.cta_add),
                color = LocalAppColors.current.light,
                fontSize = FontSize.SemiLarge,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
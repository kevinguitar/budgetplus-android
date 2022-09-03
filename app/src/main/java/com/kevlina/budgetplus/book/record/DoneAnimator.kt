package com.kevlina.budgetplus.book.record

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieAnimatable
import com.airbnb.lottie.compose.rememberLottieComposition
import com.kevlina.budgetplus.R
import com.kevlina.budgetplus.book.record.vm.RecordViewModel
import com.kevlina.budgetplus.utils.consumeEach
import kotlinx.coroutines.flow.launchIn

@Composable
fun BoxScope.DoneAnimator() {

    val viewModel = hiltViewModel<RecordViewModel>()
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    var showAnimation by remember { mutableStateOf(false) }
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.img_done))
    val lottieAnimatable = rememberLottieAnimatable()

    LaunchedEffect(key1 = viewModel) {
        viewModel.recordEvent
            .consumeEach {
                focusManager.clearFocus()
                viewModel.showFullScreenAdIfNeeded(context)

                showAnimation = true
                lottieAnimatable.animate(composition)
                showAnimation = false
            }
            .launchIn(this)
    }

    if (showAnimation) {

        LottieAnimation(
            composition = composition,
            progress = { lottieAnimatable.progress },
            modifier = Modifier
                .size(240.dp)
                .align(Alignment.Center),
        )
    }
}
package com.kevlina.budgetplus.core.lottie

import budgetplus.core.ui.generated.resources.Res
import io.github.alexzhirkevich.compottie.DotLottie
import io.github.alexzhirkevich.compottie.LottieCompositionSpec

suspend fun loadLottieSpec(fileName: String): LottieCompositionSpec {
    return LottieCompositionSpec.DotLottie(
        Res.readBytes("files/$fileName.lottie")
    )
}
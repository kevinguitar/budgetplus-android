package com.kevingt.budgetplus.monetize

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.kevingt.budgetplus.R
import kotlin.math.roundToInt

enum class AdsMode {
    Banner, Adaptive
}

@SuppressLint("VisibleForTests")
@Composable
fun AdsBanner(mode: AdsMode) {

    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {

        val boxWidth = maxWidth.value.roundToInt()

        AndroidView(
            modifier = Modifier.fillMaxWidth(),
            factory = { context ->
                AdView(context).apply {
                    setAdSize(
                        when (mode) {
                            AdsMode.Banner -> AdSize.BANNER
                            AdsMode.Adaptive -> AdSize.getCurrentOrientationInlineAdaptiveBannerAdSize(
                                context,
                                boxWidth
                            )
                        }
                    )
                    adUnitId = context.getString(R.string.admob_banner_id)
                    loadAd(AdRequest.Builder().build())
                }
            }
        )
    }
}
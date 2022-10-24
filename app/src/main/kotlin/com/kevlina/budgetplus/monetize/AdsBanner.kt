package com.kevlina.budgetplus.monetize

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.kevlina.budgetplus.core.common.R
import kotlin.math.roundToInt

@SuppressLint("VisibleForTests")
@Composable
fun AdsBanner() {

    BoxWithConstraints(
        modifier = Modifier
            .height(50.dp)
            .fillMaxWidth()
    ) {

        val boxWidth = maxWidth.value.roundToInt()

        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                AdView(context).apply {
                    setAdSize(
                        AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(
                            /* context = */ context,
                            /* width = */ boxWidth
                        )
                    )
                    adUnitId = context.getString(R.string.admob_banner_id)
                    loadAd(AdRequest.Builder().build())
                }
            }
        )
    }
}
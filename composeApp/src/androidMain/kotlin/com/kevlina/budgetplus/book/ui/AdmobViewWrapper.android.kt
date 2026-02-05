package com.kevlina.budgetplus.book.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError

@Composable
internal actual fun AdmobViewWrapper(
    isAdMobInitialized: Boolean,
    bannerId: String,
    onStateUpdate: (AdBannerState) -> Unit
) {
    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { context ->
            AdView(context).apply {
                setAdSize(AdSize.BANNER)
                adUnitId = bannerId
                adListener = object : AdListener() {
                    override fun onAdFailedToLoad(adError: LoadAdError) {
                        onStateUpdate(AdBannerState.NotAvailable)
                    }

                    override fun onAdLoaded() {
                        onStateUpdate(AdBannerState.Loaded)
                    }
                }
            }
        }
    ) { adView ->
        if (isAdMobInitialized) {
            adView.loadAd(AdRequest.Builder().build())
        }
    }
}
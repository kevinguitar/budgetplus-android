package com.kevingt.budgetplus.monetize

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@SuppressLint("VisibleForTests")
@Singleton
class FullScreenAdsLoader @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val adState = MutableStateFlow<InterstitialAd?>(null)

    init {
        loadAd()
    }

    /**
     *  Show Ad and load the next one immediately.
     */
    fun showAd(activity: Activity) {
        adState.value?.show(activity)
        loadAd()
    }

    private fun loadAd() {
        InterstitialAd.load(
            /* context = */ context,
            /* adUnitId = */ "ca-app-pub-1715595826940818/8759100223",
            /* adRequest = */ AdRequest.Builder().build(),
            /* loadCallback = */ object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Timber.e(adError.message)
                    adState.value = null
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    adState.value = interstitialAd
                }
            }
        )
    }
}
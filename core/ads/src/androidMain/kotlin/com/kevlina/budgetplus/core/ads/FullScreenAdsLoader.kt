package com.kevlina.budgetplus.core.ads

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import budgetplus.core.common.generated.resources.Res
import budgetplus.core.common.generated.resources.admob_interstitial_id
import co.touchlab.kermit.Logger
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.kevlina.budgetplus.core.common.AppCoroutineScope
import com.kevlina.budgetplus.core.common.Tracker
import com.kevlina.budgetplus.core.data.AuthManager
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString

@SingleIn(AppScope::class)
@Inject
class FullScreenAdsLoader(
    private val context: Context,
    @AppCoroutineScope appScope: CoroutineScope,
    private val adMobInitializer: AdMobInitializer,
    private val authManager: AuthManager,
    private val tracker: Tracker,
) {

    private val adState = MutableStateFlow<InterstitialAd?>(null)

    init {
        authManager.isPremium
            .filter { !it }
            .distinctUntilChanged()
            .onEach {
                adMobInitializer.ensureInitialized()
                loadAd()
            }
            .launchIn(appScope)
    }

    /**
     *  Show Ad and load the next one immediately.
     */
    fun showAd(activity: ComponentActivity) {
        if (authManager.isPremium.value) return

        adState.value?.show(activity)
        activity.lifecycleScope.launch {
            loadAd()
            tracker.logEvent("show_ad_full_screen")
        }
    }

    private suspend fun loadAd() {
        InterstitialAd.load(
            /* context = */ context,
            /* adUnitId = */ getString(Res.string.admob_interstitial_id),
            /* adRequest = */ AdRequest.Builder().build(),
            /* loadCallback = */ object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                Logger.i { adError.message }
                adState.value = null
            }

            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                adState.value = interstitialAd
            }
        })
    }
}
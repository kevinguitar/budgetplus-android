package com.kevlina.budgetplus.book.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.SearchOff
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.airbnb.lottie.compose.rememberLottieDynamicProperties
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.kevlina.budgetplus.core.common.R
import com.kevlina.budgetplus.core.lottie.rememberColorProperty
import com.kevlina.budgetplus.core.theme.LocalAppColors
import com.kevlina.budgetplus.core.theme.ThemeColors
import com.kevlina.budgetplus.core.ui.AppTheme
import com.kevlina.budgetplus.core.ui.Icon
import com.kevlina.budgetplus.core.ui.Text

@Composable
internal fun AdsBanner(
    isAdMobInitialized: Boolean,
    bannerId: String = stringResource(id = R.string.admob_banner_id_30sec),
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .height(50.dp)
            .fillMaxWidth()
    ) {

        var adBannerState by remember { mutableStateOf(AdBannerState.Loading) }
        when (adBannerState) {
            AdBannerState.Loading -> AdsBannerLoader()
            AdBannerState.NotAvailable -> AdsBannerNotAvailable()
            AdBannerState.Loaded -> Unit
        }

        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                AdView(context).apply {
                    setAdSize(AdSize.BANNER)
                    adUnitId = bannerId
                    adListener = object : AdListener() {
                        override fun onAdFailedToLoad(adError: LoadAdError) {
                            adBannerState = AdBannerState.NotAvailable
                        }

                        override fun onAdLoaded() {
                            adBannerState = AdBannerState.Loaded
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
}

@Composable
private fun AdsBannerLoader() {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.img_loader))
    val dotColor = LocalAppColors.current.dark
    val dynamicProperties = rememberLottieDynamicProperties(
        rememberColorProperty(color = dotColor, "**", "Ellipse 1", "Fill 1"),
    )

    LottieAnimation(
        composition = composition,
        iterations = LottieConstants.IterateForever,
        dynamicProperties = dynamicProperties,
        modifier = Modifier
            .fillMaxHeight()
            .scale(2.5F)
    )
}

@Composable
private fun AdsBannerNotAvailable() {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Icon(
            imageVector = Icons.Rounded.SearchOff,
            tint = LocalAppColors.current.dark,
            modifier = Modifier.size(20.dp)
        )

        Text(
            text = stringResource(id = R.string.ads_not_available),
            color = LocalAppColors.current.dark,
        )
    }
}

internal enum class AdBannerState {
    Loading, NotAvailable, Loaded
}

@Preview(heightDp = 50, showBackground = true)
@Composable
private fun AdsBannerLoader_Preview() = AppTheme(themeColors = ThemeColors.NemoSea) {
    AdsBannerLoader()
}

@Preview(showBackground = true)
@Composable
private fun AdsBannerNotAvailable_Preview() = AppTheme {
    AdsBannerNotAvailable()
}
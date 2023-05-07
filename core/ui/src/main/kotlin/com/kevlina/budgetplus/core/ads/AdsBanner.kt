package com.kevlina.budgetplus.core.ads

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.kevlina.budgetplus.core.common.R
import com.kevlina.budgetplus.core.ui.Icon
import com.kevlina.budgetplus.core.ui.LocalAppColors
import com.kevlina.budgetplus.core.ui.Text
import kotlin.math.roundToInt

@SuppressLint("VisibleForTests")
@Composable
fun AdsBanner() {

    BoxWithConstraints(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .height(50.dp)
            .fillMaxWidth()
    ) {

        val boxWidth = maxWidth.value.roundToInt()
        val bannerId = stringResource(id = R.string.admob_banner_id)
        var showNoAdsHint by remember { mutableStateOf(false) }

        if (showNoAdsHint) {

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
                    adUnitId = bannerId
                    adListener = object : AdListener() {
                        override fun onAdFailedToLoad(p0: LoadAdError) {
                            showNoAdsHint = true
                        }
                    }
                    loadAd(AdRequest.Builder().build())
                }
            }
        )
    }
}
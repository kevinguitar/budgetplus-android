package com.kevlina.budgetplus.core.data

import com.kevlina.budgetplus.core.common.R
import com.kevlina.budgetplus.core.common.mapState
import dagger.Reusable
import javax.inject.Inject

@Reusable
class BannerAdConfig @Inject constructor(
    remoteConfig: RemoteConfig,
) {

    val mode = remoteConfig.observeString("ad_banner_mode", "default")
        .mapState { value ->
            when (value) {
                "refresh_30_sec" -> BannerAdMode.Refresh30Sec
                "refresh_60_sec" -> BannerAdMode.Refresh60Sec
                else -> BannerAdMode.AutoRefresh
            }
        }

    val bannerId = mode.mapState { mode ->
        when (mode) {
            BannerAdMode.AutoRefresh -> R.string.admob_banner_id_auto
            BannerAdMode.Refresh30Sec -> R.string.admob_banner_id_30sec
            BannerAdMode.Refresh60Sec -> R.string.admob_banner_id_60sec
        }
    }
}

enum class BannerAdMode {
    AutoRefresh, Refresh30Sec, Refresh60Sec
}
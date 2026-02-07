package com.kevlina.budgetplus.book.di

import com.kevlina.budgetplus.core.ads.AdUnitId
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Named
import dev.zacsweers.metro.Provides

@ContributesTo(AppScope::class)
interface BudgetPlusIosAppModule {

    @Provides
    fun provideAdUnitId(): AdUnitId = AdUnitId(
        banner = "ca-app-pub-3940256099942544/6300978111",
        interstitial = "ca-app-pub-3940256099942544/1033173712"
    )

    @Provides
    @Named("is_debug")
    fun provideIsDebug(): Boolean = true

    @Provides
    @Named("allow_update_fcm_token")
    fun provideAllowUpdateFcmToken(): Boolean = true
}
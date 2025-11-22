package com.kevlina.budgetplus.core.billing

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo

@Module
@ContributesTo(AppScope::class)
@InstallIn(SingletonComponent::class)
interface BillingModule {

    @Binds
    fun provideBillingController(impl: BillingControllerImpl): BillingController
}
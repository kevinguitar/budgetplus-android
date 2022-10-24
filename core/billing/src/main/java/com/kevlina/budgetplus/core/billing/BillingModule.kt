package com.kevlina.budgetplus.core.billing

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal interface BillingModule {

    @Binds
    fun provideBillingController(impl: BillingControllerImpl): BillingController
}
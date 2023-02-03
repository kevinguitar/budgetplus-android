package com.kevlina.budgetplus.inapp.review

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal interface InAppReviewModule {

    @Binds
    fun provideInAppReviewManager(impl: InAppReviewManagerImpl): InAppReviewManager
}
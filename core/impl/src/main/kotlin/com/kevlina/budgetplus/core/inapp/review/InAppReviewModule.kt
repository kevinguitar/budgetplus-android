package com.kevlina.budgetplus.core.inapp.review

import android.content.Context
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import com.kevlina.budgetplus.inapp.review.InAppReviewManager
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal interface InAppReviewModule {

    @Binds
    fun provideInAppReviewManager(impl: InAppReviewManagerImpl): InAppReviewManager

    companion object {

        @Provides
        fun provideReviewManager(@ApplicationContext context: Context): ReviewManager {
            return ReviewManagerFactory.create(context)
        }
    }
}
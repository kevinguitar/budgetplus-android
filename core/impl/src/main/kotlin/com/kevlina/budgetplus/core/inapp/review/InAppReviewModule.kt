package com.kevlina.budgetplus.core.inapp.review

import android.content.Context
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides

@ContributesTo(AppScope::class)
interface InAppReviewModule {

    @Provides
    fun provideReviewManager(context: Context): ReviewManager {
        return ReviewManagerFactory.create(context)
    }
}
package com.kevlina.budgetplus.core.inapp.review

import android.content.Context
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding

@ContributesBinding(AppScope::class)
class ReviewManagerImpl(context: Context) : ReviewManager by ReviewManagerFactory.create(context)
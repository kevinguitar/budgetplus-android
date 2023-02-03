package com.kevlina.budgetplus.inapp.review

import android.app.Activity
import android.content.Context
import com.google.android.play.core.ktx.launchReview
import com.google.android.play.core.ktx.requestReview
import com.google.android.play.core.review.ReviewManagerFactory
import com.kevlina.budgetplus.core.data.local.PreferenceHolder
import com.kevlina.budgetplus.inapp.review.InAppReviewManagerImpl.Companion.INSTALL_DAYS_MIN
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import java.time.LocalDateTime
import java.time.ZoneOffset
import javax.inject.Inject

/**
 *  This implementation controls whether we should launch the review flow or not, depends on a
 *  couple conditions. We only request the review when the user uses the app for more than
 *  [INSTALL_DAYS_MIN] days, and will only request the review once per app installation.
 */
internal class InAppReviewManagerImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    preferenceHolder: PreferenceHolder,
) : InAppReviewManager {

    private val reviewManager = ReviewManagerFactory.create(context)

    private var hasRequestedBefore by preferenceHolder.bindBoolean(false)
    private var firstInitDatetime by preferenceHolder.bindLong(0)

    init {
        if (firstInitDatetime == 0L) {
            firstInitDatetime = LocalDateTime.now().minusDays(5).toEpochSecond(ZoneOffset.UTC)
        }
    }

    override fun isEligibleForReview(): Boolean {
        if (hasRequestedBefore) {
            return false
        }

        val initDateTime = LocalDateTime.ofEpochSecond(firstInitDatetime, 0, ZoneOffset.UTC)
        val now = LocalDateTime.now()

        return initDateTime.plusDays(INSTALL_DAYS_MIN).isBefore(now)
    }

    override suspend fun launchReviewFlow(activity: Activity) {
        try {
            val reviewInfo = reviewManager.requestReview()
            reviewManager.launchReview(activity, reviewInfo)
            hasRequestedBefore = true
        } catch (e: Exception) {
            Timber.e(e, "Failed to launch the review flow")
        }
    }

    companion object {
        private const val INSTALL_DAYS_MIN: Long = 3L
    }
}
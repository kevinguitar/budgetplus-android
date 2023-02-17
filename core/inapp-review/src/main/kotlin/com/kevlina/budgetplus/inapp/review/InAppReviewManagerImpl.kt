package com.kevlina.budgetplus.inapp.review

import android.app.Activity
import com.google.android.play.core.ktx.launchReview
import com.google.android.play.core.ktx.requestReview
import com.google.android.play.core.review.ReviewManager
import com.kevlina.budgetplus.core.common.Toaster
import com.kevlina.budgetplus.core.common.Tracker
import com.kevlina.budgetplus.core.data.local.PreferenceHolder
import com.kevlina.budgetplus.inapp.review.InAppReviewManagerImpl.Companion.INSTALL_DAYS_MIN
import timber.log.Timber
import java.time.LocalDateTime
import java.time.ZoneOffset
import javax.inject.Inject

/**
 *  This implementation controls whether we should launch the review flow or not, depends on a
 *  few conditions. We only request the review when the user uses the app for more than
 *  [INSTALL_DAYS_MIN] days, and will only request the review once per app installation, also,
 *  if the user rejected, we'll never prompt the review flow again for them.
 */
internal class InAppReviewManagerImpl @Inject constructor(
    private val reviewManager: ReviewManager,
    private val toaster: Toaster,
    private val tracker: Tracker,
    preferenceHolder: PreferenceHolder,
) : InAppReviewManager {

    private val now get() = LocalDateTime.now()

    private var hasRejectedBefore by preferenceHolder.bindBoolean(false)
    private var hasRequestedBefore by preferenceHolder.bindBoolean(false)
    private var firstInitDatetime by preferenceHolder.bindLong(0L)

    init {
        if (firstInitDatetime == 0L) {
            firstInitDatetime = now.toEpochSecond(ZoneOffset.UTC)
        }
    }

    override fun isEligibleForReview(): Boolean {
        if (hasRejectedBefore || hasRequestedBefore) {
            return false
        }

        val initDateTime = LocalDateTime.ofEpochSecond(firstInitDatetime, 0, ZoneOffset.UTC)
        val eligible = initDateTime.plusDays(INSTALL_DAYS_MIN).isBefore(now)
        if (eligible) {
            tracker.logEvent("inapp_review_requested")
        }
        return eligible
    }

    override suspend fun launchReviewFlow(activity: Activity) {
        try {
            val reviewInfo = reviewManager.requestReview()
            reviewManager.launchReview(activity, reviewInfo)
            tracker.logEvent("inapp_review_accepted")
            hasRequestedBefore = true
        } catch (e: Exception) {
            toaster.showError(e)
            Timber.e(e, "Failed to launch the review flow")
        }
    }

    override fun rejectReviewing() {
        tracker.logEvent("inapp_review_rejected")
        hasRejectedBefore = true
    }

    companion object {
        private const val INSTALL_DAYS_MIN: Long = 3L
    }
}
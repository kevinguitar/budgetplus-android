package com.kevlina.budgetplus.inapp.review

import android.app.Activity
import com.google.android.play.core.ktx.launchReview
import com.google.android.play.core.ktx.requestReview
import com.google.android.play.core.review.ReviewManager
import com.kevlina.budgetplus.core.common.Toaster
import com.kevlina.budgetplus.core.data.local.PreferenceHolder
import com.kevlina.budgetplus.inapp.review.InAppReviewManagerImpl.Companion.INSTALL_DAYS_MIN
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
    private val reviewManager: ReviewManager,
    private val toaster: Toaster,
    preferenceHolder: PreferenceHolder,
) : InAppReviewManager {

    private val now get() = LocalDateTime.now()

    private var hasRejectedBefore by preferenceHolder.bindBoolean(false)
    private var hasRequestedBefore by preferenceHolder.bindBoolean(false)
    private var firstInitDatetime by preferenceHolder.bindLong(
        //TODO: Remove the minus 5 days workaround!!
        now.minusDays(5).toEpochSecond(ZoneOffset.UTC)
    )

    override fun isEligibleForReview(): Boolean {
        if (hasRejectedBefore || hasRequestedBefore) {
            return false
        }

        val initDateTime = LocalDateTime.ofEpochSecond(firstInitDatetime, 0, ZoneOffset.UTC)
        return initDateTime.plusDays(INSTALL_DAYS_MIN).isBefore(now)
    }

    override suspend fun launchReviewFlow(activity: Activity) {
        try {
            val reviewInfo = reviewManager.requestReview()
            reviewManager.launchReview(activity, reviewInfo)
            hasRequestedBefore = true
        } catch (e: Exception) {
            toaster.showError(e)
            Timber.e(e, "Failed to launch the review flow")
        }
    }

    override fun rejectReviewing() {
        hasRejectedBefore = true
    }

    companion object {
        private const val INSTALL_DAYS_MIN: Long = 3L
    }
}
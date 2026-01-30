package com.kevlina.budgetplus.core.inapp.review

import android.app.Activity
import co.touchlab.kermit.Logger
import com.google.android.play.core.ktx.launchReview
import com.google.android.play.core.ktx.requestReview
import com.google.android.play.core.review.ReviewManager
import com.kevlina.budgetplus.core.common.SnackbarSender
import com.kevlina.budgetplus.core.common.Tracker
import com.kevlina.budgetplus.core.common.now
import com.kevlina.budgetplus.core.data.local.PreferenceHolder
import com.kevlina.budgetplus.core.inapp.review.InAppReviewManagerImpl.Companion.INSTALL_DAYS_MIN
import com.kevlina.budgetplus.inapp.review.InAppReviewManager
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlin.time.Duration.Companion.days
import kotlin.time.Instant

/**
 *  This implementation controls whether we should launch the review flow or not, depends on a
 *  few conditions. We only request the review when the user uses the app for more than
 *  [INSTALL_DAYS_MIN] days, and will only request the review once per app installation, also,
 *  if the user rejected, we'll never prompt the review flow again for them.
 */
@ContributesBinding(AppScope::class)
class InAppReviewManagerImpl(
    private val reviewManager: ReviewManager,
    private val snackbarSender: SnackbarSender,
    private val tracker: Tracker,
    preferenceHolder: PreferenceHolder,
) : InAppReviewManager {

    private val now get() = LocalDateTime.now()

    private var hasRejectedBefore by preferenceHolder.bindBoolean(false)
    private var hasRequestedBefore by preferenceHolder.bindBoolean(false)
    //TODO: Consider refactor to store DateTime directly
    private var firstInitDatetime by preferenceHolder.bindLong(0L)

    init {
        if (firstInitDatetime == 0L) {
            firstInitDatetime = now.toInstant(TimeZone.UTC).epochSeconds
        }
    }

    override fun isEligibleForReview(): Boolean {
        if (hasRejectedBefore || hasRequestedBefore) {
            return false
        }

        val initDateTime = Instant.fromEpochSeconds(firstInitDatetime)
        val eligible = initDateTime.plus(INSTALL_DAYS_MIN.days) < now.toInstant(TimeZone.UTC)
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
            snackbarSender.sendError(e)
            Logger.e(e) { "Failed to launch the review flow" }
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
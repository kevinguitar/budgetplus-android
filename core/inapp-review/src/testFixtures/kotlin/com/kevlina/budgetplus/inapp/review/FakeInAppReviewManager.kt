package com.kevlina.budgetplus.inapp.review

import android.app.Activity

class FakeInAppReviewManager(
    private val isEligibleForReview: Boolean = true,
) : InAppReviewManager {

    var hasLaunchedReviewFlow = false
    var hasRejectedReviewing = false

    override fun isEligibleForReview(): Boolean = isEligibleForReview

    override suspend fun launchReviewFlow(activity: Activity) {
        hasLaunchedReviewFlow = true
    }

    override fun rejectReviewing() {
        hasRejectedReviewing = true
    }
}
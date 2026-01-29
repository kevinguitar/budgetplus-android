package com.kevlina.budgetplus.inapp.review

import android.app.Activity

interface InAppReviewManager {

    fun isEligibleForReview(): Boolean

    suspend fun launchReviewFlow(activity: Activity)

    fun rejectReviewing()

}
package com.kevlina.budgetplus.inapp.review

import android.app.Activity

interface InAppReviewManager {

    suspend fun isEligibleForReview(): Boolean

    suspend fun launchReviewFlow(activity: Activity)

    fun rejectReviewing()

}
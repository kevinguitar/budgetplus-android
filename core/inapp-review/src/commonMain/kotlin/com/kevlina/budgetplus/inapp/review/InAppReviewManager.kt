package com.kevlina.budgetplus.inapp.review

interface InAppReviewManager {

    suspend fun isEligibleForReview(): Boolean

    suspend fun launchReviewFlow()

    fun rejectReviewing()

}
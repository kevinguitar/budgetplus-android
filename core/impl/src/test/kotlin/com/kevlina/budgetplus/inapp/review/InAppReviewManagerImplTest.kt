package com.kevlina.budgetplus.inapp.review

import com.google.android.play.core.review.testing.FakeReviewManager
import com.google.common.truth.Truth.assertThat
import com.kevlina.budgetplus.core.common.FakeSnackbarSender
import com.kevlina.budgetplus.core.common.FakeTracker
import com.kevlina.budgetplus.core.data.local.FakePreferenceHolder
import com.kevlina.budgetplus.core.inapp.review.InAppReviewManagerImpl
import io.mockk.mockk
import org.junit.Test
import java.time.LocalDateTime
import java.time.ZoneOffset

class InAppReviewManagerImplTest {

    @Test
    fun `WHEN the app is fresh install THEN is not eligible for review`() {
        val reviewManager = createReviewManager(firstInitDatetime = ineligibleTime)
        assertThat(reviewManager.isEligibleForReview()).isFalse()
    }

    @Test
    fun `WHEN the user already rejected before THEN is not eligible for review`() {
        val reviewManager = createReviewManager(hasRejectedBefore = true)
        assertThat(reviewManager.isEligibleForReview()).isFalse()
    }

    @Test
    fun `WHEN we already requested the review before THEN is not eligible for review`() {
        val reviewManager = createReviewManager(hasRequestedBefore = true)
        assertThat(reviewManager.isEligibleForReview()).isFalse()
    }

    @Test
    fun `WHEN we never requested and the app is installed more than 3 days THEN request review`() {
        val reviewManager = createReviewManager()
        assertThat(reviewManager.isEligibleForReview()).isTrue()
        assertThat(tracker.lastEventName).isEqualTo("inapp_review_requested")
    }

    private val eligibleTime = LocalDateTime.now().minusDays(5).toEpochSecond(ZoneOffset.UTC)
    private val ineligibleTime = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)

    private val tracker = FakeTracker()

    private fun createReviewManager(
        firstInitDatetime: Long = eligibleTime,
        hasRejectedBefore: Boolean = false,
        hasRequestedBefore: Boolean = false,
    ) = InAppReviewManagerImpl(
        reviewManager = FakeReviewManager(mockk()),
        snackbarSender = FakeSnackbarSender,
        tracker = tracker,
        preferenceHolder = FakePreferenceHolder {
            put("firstInitDatetime", firstInitDatetime)
            put("hasRejectedBefore", hasRejectedBefore)
            put("hasRequestedBefore", hasRequestedBefore)
        }
    )
}
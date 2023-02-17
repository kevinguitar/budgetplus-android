package com.kevlina.budgetplus.inapp.review

import com.google.android.play.core.review.testing.FakeReviewManager
import com.google.common.truth.Truth.assertThat
import com.kevlina.budgetplus.core.common.Tracker
import com.kevlina.budgetplus.core.data.local.Preference
import com.kevlina.budgetplus.core.data.local.PreferenceHolder
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.serialization.json.Json
import org.junit.Test
import java.time.LocalDateTime
import java.time.ZoneOffset

class InAppReviewManagerImplTest {

    @Test
    fun `WHEN the app is fresh install THEN is not eligible for review`() {
        every { preference.pref.getLong("firstInitDatetime", any()) } returns ineligibleTime

        val reviewManager = createReviewManager()
        assertThat(reviewManager.isEligibleForReview()).isFalse()
    }

    @Test
    fun `WHEN the user already rejected before THEN is not eligible for review`() {
        every { preference.pref.getBoolean("hasRejectedBefore", false) } returns true
        every { preference.pref.getLong("firstInitDatetime", any()) } returns eligibleTime

        val reviewManager = createReviewManager()
        assertThat(reviewManager.isEligibleForReview()).isFalse()
    }

    @Test
    fun `WHEN we already requested the review before THEN is not eligible for review`() {
        every { preference.pref.getBoolean("hasRequestedBefore", false) } returns true
        every { preference.pref.getLong("firstInitDatetime", any()) } returns eligibleTime

        val reviewManager = createReviewManager()
        assertThat(reviewManager.isEligibleForReview()).isFalse()
    }

    @Test
    fun `WHEN we never requested and the app is installed more than 3 days THEN request review`() {
        every { preference.pref.getLong("firstInitDatetime", any()) } returns eligibleTime

        val reviewManager = createReviewManager()
        assertThat(reviewManager.isEligibleForReview()).isTrue()
        verify {
            tracker.logEvent("inapp_review_requested")
        }
    }

    private val eligibleTime = LocalDateTime.now().minusDays(5).toEpochSecond(ZoneOffset.UTC)
    private val ineligibleTime = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)

    private val preference = mockk<Preference> {
        every { pref.getBoolean("hasRejectedBefore", false) } returns false
        every { pref.getBoolean("hasRequestedBefore", false) } returns false
    }

    private val tracker = mockk<Tracker>(relaxUnitFun = true)

    private fun createReviewManager() = InAppReviewManagerImpl(
        reviewManager = FakeReviewManager(mockk()),
        toaster = mockk(),
        tracker = tracker,
        preferenceHolder = PreferenceHolder(preference, Json)
    )
}
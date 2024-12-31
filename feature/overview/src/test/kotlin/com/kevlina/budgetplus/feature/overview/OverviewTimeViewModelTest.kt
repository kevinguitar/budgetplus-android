package com.kevlina.budgetplus.feature.overview

import com.google.common.truth.Truth.assertThat
import com.kevlina.budgetplus.core.common.FakeSnackbarSender
import com.kevlina.budgetplus.core.common.FakeTracker
import com.kevlina.budgetplus.core.common.R
import com.kevlina.budgetplus.core.common.test.MainDispatcherRule
import com.kevlina.budgetplus.core.data.FakeAuthManager
import com.kevlina.budgetplus.core.data.FakeBookRepo
import com.kevlina.budgetplus.core.data.RecordsObserver
import com.kevlina.budgetplus.core.data.remote.TimePeriod
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate

class OverviewTimeViewModelTest {

    @get:Rule
    val rule = MainDispatcherRule()

    @Test
    fun `setting the period by clicking on previous day`() = runTest {
        every { recordsObserver.timePeriod } returns flowOf(oneDayPeriod)

        val model = createModel()
        model.previousDay()

        val yesterday = LocalDate.now().minusDays(1)
        verify {
            recordsObserver.setTimePeriod(bookId, TimePeriod.Custom(yesterday, yesterday))
        }
    }

    @Test
    fun `setting the period by clicking on next day`() = runTest {
        every { recordsObserver.timePeriod } returns flowOf(oneDayPeriod)

        val model = createModel()
        model.nextDay()

        val tomorrow = LocalDate.now().plusDays(1)
        verify {
            recordsObserver.setTimePeriod(bookId, TimePeriod.Custom(tomorrow, tomorrow))
        }
    }

    @Test
    fun `the fromDate is picked when it's a one day period`() = runTest {
        every { recordsObserver.timePeriod } returns flowOf(oneDayPeriod)

        val model = createModel()
        val oneWeekAfter = LocalDate.now().plusWeeks(1)
        model.setFromDate(oneWeekAfter)

        verify {
            recordsObserver.setTimePeriod(bookId, TimePeriod.Custom(oneWeekAfter, oneWeekAfter))
        }
    }

    @Test
    fun `WHEN the fromDate is picked after the current untilDate THEN keep the current days in between`() = runTest {
        every { recordsObserver.timePeriod } returns flowOf(TimePeriod.Custom(
            from = LocalDate.now(),
            until = LocalDate.now().plusDays(3)
        ))

        val model = createModel()
        val newFromDate = LocalDate.now().plusDays(10)
        model.setFromDate(newFromDate)

        verify {
            recordsObserver.setTimePeriod(bookId, TimePeriod.Custom(
                from = newFromDate,
                until = newFromDate.plusDays(3)
            ))
        }
    }

    @Test
    fun `WHEN the period is more than one month THEN make it one month, and navigate to unlock premium`() = runTest {
        every { recordsObserver.timePeriod } returns flowOf(oneDayPeriod)

        val model = createModel()
        model.setTimePeriod(TimePeriod.Custom(
            from = LocalDate.now(),
            until = LocalDate.now().plusMonths(3)
        ))

        assertThat(model.openPremiumEvent.value.consume()).isEqualTo(Unit)
        verify {
            FakeSnackbarSender.lastSentMessageId = R.string.overview_exceed_max_period
            recordsObserver.setTimePeriod(bookId, TimePeriod.Custom(
                from = LocalDate.now(),
                until = LocalDate.now().plusMonths(1)
            ))
        }
    }

    private val bookId = "my_book"
    private val oneDayPeriod = TimePeriod.Custom(LocalDate.now(), LocalDate.now())

    private val recordsObserver = mockk<RecordsObserver> {
        every { setTimePeriod(bookId, any()) } just runs
    }

    private fun TestScope.createModel() = OverviewTimeViewModel(
        recordsObserver = recordsObserver,
        bookRepo = FakeBookRepo(currentBookId = bookId),
        authManager = FakeAuthManager(),
        snackbarSender = FakeSnackbarSender,
        tracker = FakeTracker(),
    ).apply {
        backgroundScope.launch(rule.testDispatcher) {
            timePeriod.collect()
        }
    }
}
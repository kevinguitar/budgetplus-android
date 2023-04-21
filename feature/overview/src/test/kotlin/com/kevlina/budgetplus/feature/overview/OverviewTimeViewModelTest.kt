package com.kevlina.budgetplus.feature.overview

import com.kevlina.budgetplus.core.data.AuthManager
import com.kevlina.budgetplus.core.data.BookRepo
import com.kevlina.budgetplus.core.data.RecordsObserver
import com.kevlina.budgetplus.core.data.remote.TimePeriod
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Test
import java.time.LocalDate

class OverviewTimeViewModelTest {

    @Test
    fun `setting the period by clicking on previous day`() {
        every { recordsObserver.timePeriod } returns MutableStateFlow(oneDayPeriod)

        val model = createModel()
        model.previousDay()

        val yesterday = LocalDate.now().minusDays(1)
        verify {
            recordsObserver.setTimePeriod(bookId, TimePeriod.Custom(yesterday, yesterday))
        }
    }

    @Test
    fun `setting the period by clicking on next day`() {
        every { recordsObserver.timePeriod } returns MutableStateFlow(oneDayPeriod)

        val model = createModel()
        model.nextDay()

        val tomorrow = LocalDate.now().plusDays(1)
        verify {
            recordsObserver.setTimePeriod(bookId, TimePeriod.Custom(tomorrow, tomorrow))
        }
    }

    @Test
    fun `the fromDate is picked when it's a one day period`() {
        every { recordsObserver.timePeriod } returns MutableStateFlow(oneDayPeriod)

        val model = createModel()
        val oneWeekAfter = LocalDate.now().plusWeeks(1)
        model.setFromDate(oneWeekAfter)

        verify {
            recordsObserver.setTimePeriod(bookId, TimePeriod.Custom(oneWeekAfter, oneWeekAfter))
        }
    }

    @Test
    fun `WHEN the fromDate is picked after the current untilDate THEN keep the current days in between`() {
        every { recordsObserver.timePeriod } returns MutableStateFlow(TimePeriod.Custom(
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
    fun `WHEN the period is more than one month THEN make it one month`() {
        every { recordsObserver.timePeriod } returns MutableStateFlow(oneDayPeriod)

        val model = createModel()
        model.setTimePeriod(TimePeriod.Custom(
            from = LocalDate.now(),
            until = LocalDate.now().plusMonths(3)
        ))

        verify {
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

    private val bookRepo = mockk<BookRepo> {
        every { currentBookId } returns bookId
    }

    private val authManager = mockk<AuthManager> {
        every { isPremium } returns MutableStateFlow(false)
    }

    private fun createModel() = OverviewTimeViewModel(
        recordsObserver = recordsObserver,
        bookRepo = bookRepo,
        authManager = authManager,
        snackbarSender = mockk(relaxUnitFun = true),
        tracker = mockk(relaxUnitFun = true),
        context = mockk(relaxed = true)
    )
}
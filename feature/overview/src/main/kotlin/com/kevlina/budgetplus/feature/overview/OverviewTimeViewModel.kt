package com.kevlina.budgetplus.feature.overview

import androidx.compose.runtime.Stable
import com.kevlina.budgetplus.core.common.R
import com.kevlina.budgetplus.core.common.Toaster
import com.kevlina.budgetplus.core.common.Tracker
import com.kevlina.budgetplus.core.common.mapState
import com.kevlina.budgetplus.core.data.AuthManager
import com.kevlina.budgetplus.core.data.BookRepo
import com.kevlina.budgetplus.core.data.RecordsObserver
import com.kevlina.budgetplus.core.data.remote.TimePeriod
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import javax.inject.Inject

@Stable
internal class OverviewTimeViewModel @Inject constructor(
    private val recordsObserver: RecordsObserver,
    private val bookRepo: BookRepo,
    private val authManager: AuthManager,
    private val toaster: Toaster,
    private val tracker: Tracker,
) {

    val timePeriod = recordsObserver.timePeriod
    val fromDate = timePeriod.mapState { it.from }
    val untilDate = timePeriod.mapState { it.until }
    val isOneDayPeriod = timePeriod.mapState { it.from == it.until }

    fun setTimePeriod(timePeriod: TimePeriod) {
        val bookId = bookRepo.currentBookId ?: return
        val isAboveOneMonth = timePeriod.from.isBefore(timePeriod.until.minusMonths(1))
        val period = if (!authManager.isPremium.value && isAboveOneMonth) {
            toaster.showMessage(R.string.overview_exceed_max_period)
            tracker.logEvent("overview_exceed_max_period")
            TimePeriod.Custom(
                from = timePeriod.from,
                until = timePeriod.from.plusMonths(1)
            )
        } else {
            timePeriod
        }

        recordsObserver.setTimePeriod(bookId, period)
    }

    fun setFromDate(from: LocalDate) {
        val newPeriod = when {
            isOneDayPeriod.value -> TimePeriod.Custom(from = from, until = from)
            from.isAfter(untilDate.value) -> {
                val daysInBetween = ChronoUnit.DAYS.between(fromDate.value, untilDate.value)
                TimePeriod.Custom(from = from, until = from.plusDays(daysInBetween))
            }

            else -> TimePeriod.Custom(from = from, until = untilDate.value)
        }
        setTimePeriod(newPeriod)
    }

    fun setUntilDate(until: LocalDate) {
        setTimePeriod(TimePeriod.Custom(from = fromDate.value, until = until))
    }

    fun previousDay() {
        val previousDay = fromDate.value.minusDays(1)
        setTimePeriod(TimePeriod.Custom(from = previousDay, until = previousDay))
    }

    fun nextDay() {
        val previousDay = fromDate.value.plusDays(1)
        setTimePeriod(TimePeriod.Custom(from = previousDay, until = previousDay))
    }
}
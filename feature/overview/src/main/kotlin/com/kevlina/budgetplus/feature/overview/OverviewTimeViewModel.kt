package com.kevlina.budgetplus.feature.overview

import androidx.compose.runtime.Stable
import com.kevlina.budgetplus.core.common.EventFlow
import com.kevlina.budgetplus.core.common.MutableEventFlow
import com.kevlina.budgetplus.core.common.R
import com.kevlina.budgetplus.core.common.StringProvider
import com.kevlina.budgetplus.core.common.Tracker
import com.kevlina.budgetplus.core.common.mapState
import com.kevlina.budgetplus.core.common.sendEvent
import com.kevlina.budgetplus.core.data.AuthManager
import com.kevlina.budgetplus.core.data.BookRepo
import com.kevlina.budgetplus.core.data.RecordsObserver
import com.kevlina.budgetplus.core.data.remote.TimePeriod
import com.kevlina.budgetplus.core.ui.Book
import com.kevlina.budgetplus.core.ui.SnackbarData
import com.kevlina.budgetplus.core.ui.SnackbarSender
import com.kevlina.budgetplus.core.ui.wrapped
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import javax.inject.Inject

@Stable
class OverviewTimeViewModel @Inject constructor(
    private val recordsObserver: RecordsObserver,
    private val bookRepo: BookRepo,
    private val authManager: AuthManager,
    private val tracker: Tracker,
    private val stringProvider: StringProvider,
    @Book private val snackbarSender: SnackbarSender,
) {

    val timePeriod = recordsObserver.timePeriod
    val fromDate = timePeriod.mapState { it.from.wrapped() }
    val untilDate = timePeriod.mapState { it.until.wrapped() }
    val isOneDayPeriod = timePeriod.mapState { it.from == it.until }

    private val _openPremiumEvent = MutableEventFlow<Unit>()
    val openPremiumEvent: EventFlow<Unit> get() = _openPremiumEvent

    fun setTimePeriod(timePeriod: TimePeriod) {
        val bookId = bookRepo.currentBookId ?: return
        val isAboveOneMonth = timePeriod.from.isBefore(timePeriod.until.minusMonths(1))
        val period = if (!authManager.isPremium.value && isAboveOneMonth) {
            tracker.logEvent("overview_exceed_max_period")
            snackbarSender.showSnackbar(SnackbarData(
                message = stringProvider[R.string.overview_exceed_max_period],
                actionLabel = stringProvider[R.string.cta_go],
                action = {
                    tracker.logEvent("overview_exceed_max_period_unlock")
                    _openPremiumEvent.sendEvent()
                }
            ))

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
            from.isAfter(untilDate.value.value) -> {
                val daysInBetween = ChronoUnit.DAYS.between(fromDate.value.value, untilDate.value.value)
                TimePeriod.Custom(from = from, until = from.plusDays(daysInBetween))
            }

            else -> TimePeriod.Custom(from = from, until = untilDate.value.value)
        }
        setTimePeriod(newPeriod)
    }

    fun setUntilDate(until: LocalDate) {
        setTimePeriod(TimePeriod.Custom(from = fromDate.value.value, until = until))
    }

    fun previousDay() {
        val previousDay = fromDate.value.value.minusDays(1)
        setTimePeriod(TimePeriod.Custom(from = previousDay, until = previousDay))
    }

    fun nextDay() {
        val previousDay = fromDate.value.value.plusDays(1)
        setTimePeriod(TimePeriod.Custom(from = previousDay, until = previousDay))
    }
}
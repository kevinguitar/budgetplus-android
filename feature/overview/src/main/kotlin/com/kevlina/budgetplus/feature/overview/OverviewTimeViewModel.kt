package com.kevlina.budgetplus.feature.overview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import javax.inject.Inject

class OverviewTimeViewModel @Inject constructor(
    private val recordsObserver: RecordsObserver,
    private val bookRepo: BookRepo,
    private val authManager: AuthManager,
    private val tracker: Tracker,
    private val stringProvider: StringProvider,
    @Book private val snackbarSender: SnackbarSender,
) : ViewModel() {

    val timePeriod = recordsObserver.timePeriod
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), TimePeriod.Month)

    val fromDate = timePeriod.mapState { it.from }
    val untilDate = timePeriod.mapState { it.until }
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
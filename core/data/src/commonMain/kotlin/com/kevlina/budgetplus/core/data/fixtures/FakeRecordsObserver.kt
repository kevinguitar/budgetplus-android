package com.kevlina.budgetplus.core.data.fixtures

import androidx.annotation.RestrictTo
import com.kevlina.budgetplus.core.data.RecordsObserver
import com.kevlina.budgetplus.core.data.remote.Record
import com.kevlina.budgetplus.core.data.remote.TimePeriod
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

@RestrictTo(RestrictTo.Scope.TESTS)
class FakeRecordsObserver(
    timePeriodFlow: Flow<TimePeriod>? = null,
    records: Sequence<Record>? = null,
) : RecordsObserver {

    override val timePeriod: Flow<TimePeriod> = timePeriodFlow ?: MutableStateFlow(
        TimePeriod.Week
    )

    override val records: MutableStateFlow<Sequence<Record>?> = MutableStateFlow(records)

    val setTimePeriodCalls = mutableListOf<Pair<String, TimePeriod>>()

    override fun setTimePeriod(bookId: String, period: TimePeriod) {
        setTimePeriodCalls.add(bookId to period)
    }
}

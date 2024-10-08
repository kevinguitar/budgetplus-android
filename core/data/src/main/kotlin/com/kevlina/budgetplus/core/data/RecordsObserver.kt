package com.kevlina.budgetplus.core.data

import com.kevlina.budgetplus.core.data.remote.Record
import com.kevlina.budgetplus.core.data.remote.TimePeriod
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface RecordsObserver {

    val timePeriod: Flow<TimePeriod>

    val records: StateFlow<Sequence<Record>?>

    fun setTimePeriod(bookId: String, period: TimePeriod)

}
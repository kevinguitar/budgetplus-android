package com.kevlina.budgetplus.core.data

import com.kevlina.budgetplus.core.data.remote.Record
import com.kevlina.budgetplus.core.data.remote.TimePeriod
import kotlinx.coroutines.flow.StateFlow

interface RecordsObserver {

    val records: StateFlow<Sequence<Record>>

    fun observeRecords(bookId: String, period: TimePeriod)

}
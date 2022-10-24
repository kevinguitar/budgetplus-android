package com.kevlina.budgetplus.core.data

import com.kevlina.budgetplus.core.data.remote.Record

interface RecordRepo {

    fun createRecord(record: Record)

    fun editRecord(recordId: String, record: Record)

    fun deleteRecord(recordId: String)

}
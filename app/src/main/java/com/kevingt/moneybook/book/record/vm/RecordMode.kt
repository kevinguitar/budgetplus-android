package com.kevingt.moneybook.book.record.vm

import com.kevingt.moneybook.data.remote.Record

sealed class RecordMode {

    object Add : RecordMode()

    data class Edit(val record: Record) : RecordMode()

}

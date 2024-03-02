package com.kevlina.budgetplus.feature.add.record

import java.time.LocalDate

sealed interface RecordDateState {

    val date: LocalDate

    data object Now : RecordDateState {
        override val date: LocalDate get() = LocalDate.now()
    }

    data class Other(override val date: LocalDate) : RecordDateState

}
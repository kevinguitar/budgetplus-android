package com.kevlina.budgetplus.feature.add.record

import com.kevlina.budgetplus.core.common.now
import kotlinx.datetime.LocalDate

sealed interface RecordDateState {

    val date: LocalDate

    data object Now : RecordDateState {
        override val date: LocalDate get() = LocalDate.now()
    }

    data class Other(override val date: LocalDate) : RecordDateState

}
package com.kevingt.budgetplus.data.remote

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
sealed class TimePeriod {

    abstract val from: LocalDate
    abstract val until: LocalDate

    @Serializable
    object Today : TimePeriod() {

        private val now get() = LocalDate.now()

        override val from: LocalDate get() = now
        override val until: LocalDate get() = now
    }

    @Serializable
    object Week : TimePeriod() {

        private val now get() = LocalDate.now()
        private val dayOfWeek get() = now.dayOfWeek.value

        override val from: LocalDate get() = now.minusDays((dayOfWeek - 1).toLong())
        override val until: LocalDate get() = now.plusDays((7 - dayOfWeek).toLong())
    }

    @Serializable
    object Month : TimePeriod() {

        private val now get() = LocalDate.now()
        private val monthDays get() = now.month.length(now.isLeapYear)

        override val from: LocalDate get() = now.withDayOfMonth(1)
        override val until: LocalDate get() = now.withDayOfMonth(monthDays)
    }

    @Serializable
    object LastMonth : TimePeriod() {

        private val now get() = LocalDate.now()
        private val monthDays
            get() = now
                .minusMonths(1).month
                .length(now.isLeapYear)

        override val from: LocalDate
            get() = now
                .minusMonths(1)
                .withDayOfMonth(1)

        override val until: LocalDate
            get() = now
                .minusMonths(1)
                .withDayOfMonth(monthDays)
    }

    @Serializable
    data class Custom(
        @Contextual override val from: LocalDate,
        @Contextual override val until: LocalDate
    ) : TimePeriod() {

        init {
            check(!from.isAfter(until)) {
                "From date is later than until."
            }
        }
    }

}

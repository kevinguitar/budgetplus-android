package com.kevingt.moneybook.book.overview.vm

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.LocalDate

sealed class TimePeriod : Parcelable {

    abstract val from: LocalDate
    abstract val until: LocalDate

    @Parcelize
    object Today : TimePeriod() {

        private val now get() = LocalDate.now()

        override val from: LocalDate get() = now
        override val until: LocalDate get() = now
    }

    @Parcelize
    object Week : TimePeriod() {

        private val now get() = LocalDate.now()
        private val dayOfWeek get() = now.dayOfWeek.value

        override val from: LocalDate get() = now.minusDays((dayOfWeek - 1).toLong())
        override val until: LocalDate get() = now.plusDays((7 - dayOfWeek).toLong())
    }

    @Parcelize
    object Month : TimePeriod() {

        private val now get() = LocalDate.now()
        private val monthDays get() = now.month.length(now.isLeapYear)

        override val from: LocalDate get() = now.withDayOfMonth(1)
        override val until: LocalDate get() = now.withDayOfMonth(monthDays)
    }

    @Parcelize
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

    @Parcelize
    data class Custom(
        val fromEpoch: Long = 0,
        val untilEpoch: Long = 0
    ) : TimePeriod() {

        override val from: LocalDate get() = LocalDate.ofEpochDay(fromEpoch)
        override val until: LocalDate get() = LocalDate.ofEpochDay(untilEpoch)

        init {
            check(!from.isAfter(until)) {
                "From date is later than until."
            }
        }
    }

}

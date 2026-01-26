package com.kevlina.budgetplus.core.data.remote

import androidx.compose.runtime.Immutable
import com.kevlina.budgetplus.core.common.shortFormatted
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Immutable
@Serializable
sealed class TimePeriod {

    abstract val from: LocalDate
    abstract val until: LocalDate

    @Serializable
    data object Today : TimePeriod() {

        private val now get() = LocalDate.now()

        override val from: LocalDate get() = now
        override val until: LocalDate get() = now
    }

    @Serializable
    data object Week : TimePeriod() {

        private val now get() = LocalDate.now()
        private val dayOfWeek get() = now.dayOfWeek.value
        private const val WEEK_DAYS = 7

        override val from: LocalDate get() = now.minusDays((dayOfWeek - 1).toLong())
        override val until: LocalDate get() = now.plusDays((WEEK_DAYS - dayOfWeek).toLong())
    }

    @Serializable
    data object Month : TimePeriod() {

        private val now get() = LocalDate.now()
        private val monthDays get() = now.month.length(now.isLeapYear)

        override val from: LocalDate get() = now.withDayOfMonth(1)
        override val until: LocalDate get() = now.withDayOfMonth(monthDays)
    }

    @Serializable
    data object LastMonth : TimePeriod() {

        private val now get() = LocalDate.now()
        private val monthDays
            get() = now
                .minusMonths(1)
                .month
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
        @Contextual override val until: LocalDate,
    ) : TimePeriod() {

        init {
            check(!from.isAfter(until)) {
                "From date is later than until."
            }
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true

            other as TimePeriod

            return from == other.from && until == other.until
        }

        override fun hashCode(): Int {
            var result = from.hashCode()
            result = 31 * result + until.hashCode()
            return result
        }
    }

    override fun toString(): String {
        return "TimePeriod from ${from.shortFormatted} to ${until.shortFormatted}"
    }
}

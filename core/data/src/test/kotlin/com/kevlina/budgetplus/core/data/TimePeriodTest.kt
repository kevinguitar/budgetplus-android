package com.kevlina.budgetplus.core.data

import com.google.common.truth.Truth.assertThat
import com.kevlina.budgetplus.core.data.remote.TimePeriod
import org.junit.Test
import java.time.DayOfWeek
import java.time.LocalDate

class TimePeriodTest {

    @Test
    fun `WHEN Today is selected THEN from is equal to until`() {
        val period = TimePeriod.Today
        assertThat(period.from).isEqualTo(LocalDate.now())
        assertThat(period.from).isEqualTo(period.until)
    }

    @Test
    fun `WHEN Week is selected THEN from is 7 days earlier than until`() {
        val period = TimePeriod.Week
        assertThat(period.from.isAfter(LocalDate.now())).isFalse()
        assertThat(period.from.dayOfWeek).isEqualTo(DayOfWeek.MONDAY)
        assertThat(period.until.dayOfWeek).isEqualTo(DayOfWeek.SUNDAY)
        assertThat(period.from.plusDays(6)).isEqualTo(period.until)
    }

    @Test
    fun `WHEN Month is selected THEN from will be the first day of the month`() {
        val period = TimePeriod.Month
        assertThat(period.from.isAfter(LocalDate.now())).isFalse()
        assertThat(period.from.month).isEqualTo(period.until.month)
        assertThat(period.from.dayOfMonth).isEqualTo(1)
        assertThat(period.from.plusMonths(1).minusDays(1))
            .isEqualTo(period.until)
    }

    @Test
    fun `WHEN LastMonth is selected THEN until will be earlier than now`() {
        val period = TimePeriod.LastMonth
        assertThat(period.until.isBefore(LocalDate.now())).isTrue()
        assertThat(period.from.month).isEqualTo(period.until.month)
        assertThat(period.from.dayOfMonth).isEqualTo(1)
        assertThat(period.from.plusMonths(1).minusDays(1))
            .isEqualTo(period.until)
    }

    @Test
    fun `WHEN Custom is selected and until is earlier than from THEN throw exception`() {
        try {
            TimePeriod.Custom(
                from = LocalDate.MAX,
                until = LocalDate.MIN
            )
        } catch (e: Exception) {
            assertThat(e.message).isEqualTo("From date is later than until.")
        }
    }

    @Test
    fun `WHEN Custom is selected and until is later than from THEN no error`() {
        val period = TimePeriod.Custom(
            from = LocalDate.now(),
            until = LocalDate.now().plusDays(1)
        )
        assertThat(period.from.isBefore(period.until)).isTrue()
    }
}
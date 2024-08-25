package com.kevlina.budgetplus.feature.add.record

import com.google.common.truth.Truth.assertThat
import com.kevlina.budgetplus.core.common.EventFlow
import com.kevlina.budgetplus.core.common.R
import com.kevlina.budgetplus.core.common.RecordType
import com.kevlina.budgetplus.core.common.impl.FakeStringProvider
import com.kevlina.budgetplus.core.common.impl.FakeToaster
import com.kevlina.budgetplus.core.common.test.MainDispatcherRule
import com.kevlina.budgetplus.core.common.withCurrentTime
import com.kevlina.budgetplus.core.data.FullScreenAdsLoader
import com.kevlina.budgetplus.core.data.impl.FakeAuthManager
import com.kevlina.budgetplus.core.data.impl.FakeBookRepo
import com.kevlina.budgetplus.core.data.impl.FakeRecordRepo
import com.kevlina.budgetplus.core.data.impl.FakeVibratorManager
import com.kevlina.budgetplus.core.data.local.FakePreferenceHolder
import com.kevlina.budgetplus.core.data.remote.Record
import com.kevlina.budgetplus.core.ui.bubble.BubbleRepo
import com.kevlina.budgetplus.feature.category.pills.CategoriesViewModel
import com.kevlina.budgetplus.inapp.review.FakeInAppReviewManager
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate

class RecordViewModelTest {

    @get:Rule
    val rule = MainDispatcherRule()

    @Test
    fun `show message when category is empty`() = runTest {
        createModel()
        categoriesVm.setCategory(null)
        calculatorVm.input("1")
        calculatorVm.evaluate()

        assertThat(toaster.lastShownMessage).isEqualTo("Category is empty")
    }

    @Test
    fun `show message when price is empty`() = runTest {
        createModel()
        calculatorVm.evaluate()

        assertThat(toaster.lastShownMessage).isEqualTo("Price is empty")
    }

    @Test
    fun `record should be created with correct info in RecordRepo`() = runTest {
        val date = LocalDate.now().minusDays(1)

        createModel().apply {
            setDate(date)
            setNote("Test note")
            setType(RecordType.Income)
        }

        calculatorVm.input("123")
        calculatorVm.evaluate()

        assertThat(recordRepo.lastCreatedRecord).isEqualTo(
            Record(
                type = RecordType.Income,
                category = "Test category",
                name = "Test note",
                date = date.toEpochDay(),
                timestamp = date.withCurrentTime,
                price = 123.0,
            )
        )
    }

    @Test
    fun `record without note should fallback to category`() = runTest {
        createModel()

        calculatorVm.input("1.23")
        calculatorVm.evaluate()

        assertThat(recordRepo.lastCreatedRecord).isEqualTo(
            Record(
                type = RecordType.Expense,
                category = "Test category",
                name = "Test category",
                date = LocalDate.now().toEpochDay(),
                timestamp = LocalDate.now().withCurrentTime,
                price = 1.23,
            )
        )
    }

    @Test
    fun `reset screen after the record is recorded`() = runTest {
        val model = createModel().apply {
            setNote("Test note")
        }

        calculatorVm.input("1")
        calculatorVm.evaluate()

        assertThat(categoriesVm.category.value).isNull()
        assertThat(model.note.value).isEmpty()
        assertThat(calculatorVm.priceText.value).isEqualTo("0")
    }

    @Test
    fun `show fullscreen ad on every 5th record`() = runTest {
        createModel(recordCount = 4)
        calculatorVm.input("1")
        calculatorVm.evaluate()

        verify(exactly = 1) { fullScreenAdsLoader.showAd(any()) }
    }

    @Test
    fun `request notification permission after the 2nd record`() = runTest {
        val model = createModel(recordCount = 1)
        calculatorVm.input("1")
        calculatorVm.evaluate()

        model.requestPermissionEvent.assertHasUnconsumedEvent()
    }

    @Test
    fun `request in app review after 4th record`() = runTest {
        val model = createModel(recordCount = 3)
        calculatorVm.input("1")
        calculatorVm.evaluate()

        model.requestReviewEvent.assertHasUnconsumedEvent()
    }


    private val calculatorVm = CalculatorViewModel(
        vibrator = FakeVibratorManager(),
        toaster = FakeToaster()
    )

    private val categoriesVm = CategoriesViewModel(bookRepo = FakeBookRepo()).apply {
        setCategory("Test category")
    }

    private val stringProvider = FakeStringProvider(
        stringMap = mapOf(
            R.string.record_created to "Record created",
            R.string.record_empty_category to "Category is empty",
            R.string.record_empty_price to "Price is empty"
        )
    )

    private val bookRepo = FakeBookRepo()
    private val recordRepo = FakeRecordRepo()
    private val toaster = FakeToaster(stringProvider)

    private val fullScreenAdsLoader = mockk<FullScreenAdsLoader>(relaxed = true)

    context(TestScope)
    private fun createModel(
        recordCount: Int = 0,
    ) = RecordViewModel(
        calculatorVm = calculatorVm,
        categoriesVm = categoriesVm,
        bookRepo = bookRepo,
        recordRepo = recordRepo,
        bubbleRepo = BubbleRepo(backgroundScope),
        authManager = FakeAuthManager(),
        fullScreenAdsLoader = fullScreenAdsLoader,
        inAppReviewManager = FakeInAppReviewManager(),
        toaster = toaster,
        stringProvider = stringProvider,
        preferenceHolder = FakePreferenceHolder {
            put("recordCount", recordCount)
        },
    )
}

private fun EventFlow<Unit>.assertHasUnconsumedEvent() {
    assertThat(value.consume()).isEqualTo(Unit)
}
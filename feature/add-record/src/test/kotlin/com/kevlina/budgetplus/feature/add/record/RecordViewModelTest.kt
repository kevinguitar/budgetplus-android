package com.kevlina.budgetplus.feature.add.record

import com.google.common.truth.Truth.assertThat
import com.kevlina.budgetplus.core.common.EventFlow
import com.kevlina.budgetplus.core.common.FakeStringProvider
import com.kevlina.budgetplus.core.common.R
import com.kevlina.budgetplus.core.common.RecordType
import com.kevlina.budgetplus.core.common.test.MainDispatcherRule
import com.kevlina.budgetplus.core.data.FakeAuthManager
import com.kevlina.budgetplus.core.data.FakeBookRepo
import com.kevlina.budgetplus.core.data.FakeRecordRepo
import com.kevlina.budgetplus.core.data.FakeVibratorManager
import com.kevlina.budgetplus.core.data.FullScreenAdsLoader
import com.kevlina.budgetplus.core.data.local.FakePreferenceHolder
import com.kevlina.budgetplus.core.data.remote.Record
import com.kevlina.budgetplus.core.ui.FakeSnackbarSender
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

        assertThat(FakeSnackbarSender.lastSentMessageId).isEqualTo(R.string.record_empty_category)
    }

    @Test
    fun `show message when price is empty`() = runTest {
        createModel()
        calculatorVm.evaluate()

        assertThat(FakeSnackbarSender.lastSentMessageId).isEqualTo(R.string.record_empty_price)
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

        assertThat(
            // Do not verify timestamp as it depends on test execution time
            FakeRecordRepo.lastCreatedRecord?.copy(timestamp = null)
        ).isEqualTo(
            Record(
                type = RecordType.Income,
                category = "Test category",
                name = "Test note",
                date = date.toEpochDay(),
                price = 123.0,
            )
        )
    }

    @Test
    fun `record without note should fallback to category`() = runTest {
        createModel()

        calculatorVm.input("1.23")
        calculatorVm.evaluate()

        assertThat(
            // Do not verify timestamp as it depends on test execution time
            FakeRecordRepo.lastCreatedRecord?.copy(timestamp = null)
        ).isEqualTo(
            Record(
                type = RecordType.Expense,
                category = "Test category",
                name = "Test category",
                date = LocalDate.now().toEpochDay(),
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
    fun `show fullscreen ad on every 7th record`() = runTest {
        createModel(recordCount = 6)
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
        snackbarSender = FakeSnackbarSender
    )

    private val bookRepo = FakeBookRepo()
    private val categoriesVm = CategoriesViewModel(bookRepo = bookRepo).apply {
        setCategory("Test category")
    }

    private val stringProvider = FakeStringProvider(
        stringMap = mapOf(
            R.string.record_created to "Record created",
            R.string.record_empty_category to "Category is empty",
            R.string.record_empty_price to "Price is empty"
        )
    )

    private val fullScreenAdsLoader = mockk<FullScreenAdsLoader>(relaxed = true)

    private fun TestScope.createModel(
        recordCount: Int = 0,
    ) = RecordViewModel(
        calculatorVm = calculatorVm,
        categoriesVm = categoriesVm,
        bookRepo = bookRepo,
        recordRepo = FakeRecordRepo,
        bubbleRepo = BubbleRepo(backgroundScope),
        authManager = FakeAuthManager(),
        fullScreenAdsLoader = fullScreenAdsLoader,
        inAppReviewManager = FakeInAppReviewManager(),
        snackbarSender = FakeSnackbarSender,
        stringProvider = stringProvider,
        preferenceHolder = FakePreferenceHolder {
            put("recordCount", recordCount)
        },
    )
}

private fun EventFlow<Unit>.assertHasUnconsumedEvent() {
    assertThat(value.consume()).isEqualTo(Unit)
}
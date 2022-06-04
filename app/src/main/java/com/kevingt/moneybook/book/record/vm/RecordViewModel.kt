package com.kevingt.moneybook.book.record.vm

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import com.kevingt.moneybook.auth.AuthManager
import com.kevingt.moneybook.data.remote.*
import com.kevingt.moneybook.utils.Toaster
import com.kevingt.moneybook.utils.mapState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class RecordViewModel @Inject constructor(
    val calculator: CalculatorViewModel,
    private val bookRepo: BookRepo,
    private val recordRepo: RecordRepo,
    private val authManager: AuthManager,
    private val toaster: Toaster,
) : ViewModel() {

    private val _type = MutableStateFlow(RecordType.Expense)
    val type: StateFlow<RecordType> = _type.asStateFlow()

    private val _date = MutableStateFlow(LocalDate.now())
    val date: StateFlow<LocalDate> = _date.asStateFlow()

    private val _category = MutableStateFlow<String?>(null)
    val category: StateFlow<String?> = _category.asStateFlow()

    private val _name = MutableStateFlow("")
    val name: StateFlow<String> = _name.asStateFlow()

    val bookName = bookRepo.bookState
        .mapState { it?.name }

    val expenseCategories = bookRepo.bookState
        .mapState { it?.expenseCategories.orEmpty() }

    val incomeCategories = bookRepo.bookState
        .mapState { it?.incomeCategories.orEmpty() }

    fun setType(type: RecordType) {
        _type.value = type
    }

    fun setDate(date: LocalDate) {
        _date.value = date
    }

    fun setCategory(category: String) {
        _category.value = category
    }

    fun setName(name: String) {
        _name.value = name
    }

    fun shareJoinLink(context: Context) {
        val bookId = bookRepo.bookIdState.value ?: return
        val joinLink = "https://moneybook.cchi.tw/join/$bookId"

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, "Click the link to track expenses together!\n$joinLink")
        }
        context.startActivity(Intent.createChooser(intent, "Invite"))
    }

    fun record() {
        calculator.evaluate()

        val category = category.value
        val price = calculator.price.value

        if (category == null) {
            toaster.showMessage("Please choose a category")
            return
        }

        if (price == 0.0) {
            toaster.showMessage("Price can't be 0")
            return
        }

        val record = Record(
            type = type.value,
            date = date.value.toEpochDay(),
            category = category,
            name = name.value,
            price = calculator.price.value,
            author = authManager.userState.value?.toAuthor()
        )
        recordRepo.createRecord(record)

        toaster.showMessage("Record created")
        resetScreen()
    }

    private fun resetScreen() {
        _category.value = null
        _name.value = ""
        calculator.clearPrice()
    }
}
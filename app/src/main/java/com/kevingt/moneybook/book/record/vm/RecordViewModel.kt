package com.kevingt.moneybook.book.record.vm

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kevingt.moneybook.R
import com.kevingt.moneybook.auth.AuthManager
import com.kevingt.moneybook.data.remote.*
import com.kevingt.moneybook.utils.Toaster
import com.kevingt.moneybook.utils.mapState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class RecordViewModel @Inject constructor(
    val calculator: CalculatorViewModel,
    private val bookRepo: BookRepo,
    private val recordRepo: RecordRepo,
    private val authManager: AuthManager,
    private val toaster: Toaster,
    @ApplicationContext private val context: Context,
) : ViewModel() {

    private val _mode = MutableStateFlow<RecordMode>(RecordMode.Add)
    val isEditing: StateFlow<Boolean> = _mode.mapState { it is RecordMode.Edit }

    private val _type = MutableStateFlow(RecordType.Expense)
    val type: StateFlow<RecordType> = _type.asStateFlow()

    private val _date = MutableStateFlow(LocalDate.now())
    val date: StateFlow<LocalDate> = _date.asStateFlow()

    private val _category = MutableStateFlow<String?>(null)
    val category: StateFlow<String?> = _category.asStateFlow()

    private val _name = MutableStateFlow("")
    val name: StateFlow<String> = _name.asStateFlow()

    val expenseCategories = bookRepo.bookState
        .mapState { it?.expenseCategories.orEmpty() }

    val incomeCategories = bookRepo.bookState
        .mapState { it?.incomeCategories.orEmpty() }

    init {
        _mode
            .filterIsInstance<RecordMode.Edit>()
            .onEach { mode ->
                with(mode.record) {
                    setType(type)
                    setDate(LocalDate.ofEpochDay(date))
                    setCategory(category)
                    setName(name)
                    calculator.editPrice(price)
                }
            }
            .launchIn(viewModelScope)
    }

    fun setEditMode(record: Record) {
        _mode.value = RecordMode.Edit(record)
    }

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
        val bookId = bookRepo.currentBookId ?: return
        val joinLink = "https://moneybook.cchi.tw/join/$bookId"

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, context.getString(R.string.menu_share_book, joinLink))
        }
        context.startActivity(Intent.createChooser(intent, context.getString(R.string.cta_invite)))
    }

    fun record() {
        calculator.evaluate()

        val category = category.value
        val price = calculator.price.value

        if (category == null) {
            toaster.showMessage(R.string.record_empty_category)
            return
        }

        if (price == 0.0) {
            toaster.showMessage(R.string.record_empty_price)
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

        val recordId = (_mode.value as? RecordMode.Edit)?.record?.id
        if (recordId == null) {
            recordRepo.createRecord(record)
            toaster.showMessage(context.getString(R.string.record_created, category))
        } else {
            recordRepo.editRecord(recordId, record)
            toaster.showMessage(R.string.record_edited)
        }

        resetScreen()
    }

    fun deleteRecord() {
        val recordId = (_mode.value as RecordMode.Edit).record.id
        recordRepo.deleteRecord(recordId)

        toaster.showMessage(R.string.record_deleted)
        resetScreen()
    }

    private fun resetScreen() {
        _category.value = null
        _name.value = ""
        calculator.clearPrice()
    }
}
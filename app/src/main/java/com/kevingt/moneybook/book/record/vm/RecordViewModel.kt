package com.kevingt.moneybook.book.record.vm

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import com.kevingt.moneybook.R
import com.kevingt.moneybook.auth.AuthManager
import com.kevingt.moneybook.book.bubble.vm.BubbleDest
import com.kevingt.moneybook.book.bubble.vm.BubbleRepo
import com.kevingt.moneybook.data.local.PreferenceHolder
import com.kevingt.moneybook.data.remote.*
import com.kevingt.moneybook.utils.Toaster
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
    private val bubbleRepo: BubbleRepo,
    private val authManager: AuthManager,
    private val toaster: Toaster,
    preferenceHolder: PreferenceHolder
) : ViewModel() {

    private val _type = MutableStateFlow(RecordType.Expense)
    val type: StateFlow<RecordType> = _type.asStateFlow()

    private val _date = MutableStateFlow(LocalDate.now())
    val date: StateFlow<LocalDate> = _date.asStateFlow()

    private val _category = MutableStateFlow<String?>(null)
    val category: StateFlow<String?> = _category.asStateFlow()

    private val _note = MutableStateFlow("")
    val note: StateFlow<String> = _note.asStateFlow()

    private var isInviteBubbleShown by preferenceHolder.bindBoolean(false)

    fun setType(type: RecordType) {
        _type.value = type
    }

    fun setDate(date: LocalDate) {
        _date.value = date
    }

    fun setCategory(category: String) {
        _category.value = category
    }

    fun setNote(note: String) {
        _note.value = note
    }

    fun shareJoinLink(context: Context) {
        val joinLink = bookRepo.generateJoinLink()
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            //TODO: Append play store link after publishing
            putExtra(Intent.EXTRA_TEXT, context.getString(R.string.menu_share_book, joinLink))
        }
        context.startActivity(Intent.createChooser(intent, context.getString(R.string.cta_invite)))
    }

    fun highlightInviteButton(dest: BubbleDest) {
        if (!isInviteBubbleShown) {
            isInviteBubbleShown = true
            bubbleRepo.setDestination(dest)
        }
    }

    fun record(): Boolean {
        calculator.evaluate()

        val category = category.value
        val price = calculator.price.value

        if (category == null) {
            toaster.showMessage(R.string.record_empty_category)
            return false
        }

        if (price == 0.0) {
            toaster.showMessage(R.string.record_empty_price)
            return false
        }

        val record = Record(
            type = type.value,
            date = date.value.toEpochDay(),
            category = category,
            name = note.value,
            price = calculator.price.value,
            author = authManager.userState.value?.toAuthor()
        )

        recordRepo.createRecord(record)
        resetScreen()
        return true
    }

    private fun resetScreen() {
        _category.value = null
        _note.value = ""
        calculator.clearPrice()
    }
}
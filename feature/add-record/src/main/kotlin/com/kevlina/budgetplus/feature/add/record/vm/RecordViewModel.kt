package com.kevlina.budgetplus.feature.add.record.vm

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kevlina.budgetplus.core.ads.FullScreenAdsLoader
import com.kevlina.budgetplus.core.common.EventFlow
import com.kevlina.budgetplus.core.common.MutableEventFlow
import com.kevlina.budgetplus.core.common.R
import com.kevlina.budgetplus.core.common.RecordType
import com.kevlina.budgetplus.core.common.Toaster
import com.kevlina.budgetplus.core.common.Tracker
import com.kevlina.budgetplus.core.common.consumeEach
import com.kevlina.budgetplus.core.common.sendEvent
import com.kevlina.budgetplus.core.data.AuthManager
import com.kevlina.budgetplus.core.data.BookRepo
import com.kevlina.budgetplus.core.data.RecordRepo
import com.kevlina.budgetplus.core.data.local.PreferenceHolder
import com.kevlina.budgetplus.core.data.remote.Record
import com.kevlina.budgetplus.core.data.remote.toAuthor
import com.kevlina.budgetplus.core.ui.bubble.BubbleDest
import com.kevlina.budgetplus.core.ui.bubble.BubbleRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
@Stable
class RecordViewModel @Inject constructor(
    val calculator: CalculatorViewModel,
    private val bookRepo: BookRepo,
    private val recordRepo: RecordRepo,
    private val bubbleRepo: BubbleRepo,
    private val authManager: AuthManager,
    private val fullScreenAdsLoader: FullScreenAdsLoader,
    private val toaster: Toaster,
    private val tracker: Tracker,
    preferenceHolder: PreferenceHolder,
    @ApplicationContext private val context: Context,
) : ViewModel() {

    private val _type = MutableStateFlow(RecordType.Expense)
    val type: StateFlow<RecordType> = _type.asStateFlow()

    private val _date = MutableStateFlow(LocalDate.now())
    val date: StateFlow<LocalDate> = _date.asStateFlow()

    private val _category = MutableStateFlow<String?>(null)
    val category: StateFlow<String?> = _category.asStateFlow()

    private val _note = MutableStateFlow("")
    val note: StateFlow<String> = _note.asStateFlow()

    private val _recordEvent = MutableEventFlow<Unit>()
    val recordEvent: EventFlow<Unit> = _recordEvent.asStateFlow()

    val isHideAds = authManager.isPremium

    private var isInviteBubbleShown by preferenceHolder.bindBoolean(false)
    private var recordCount by preferenceHolder.bindInt(0)

    init {
        calculator.recordFlow
            .consumeEach { record() }
            .launchIn(viewModelScope)
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

    fun setNote(note: String) {
        _note.value = note
    }

    fun shareJoinLink(context: Context) {
        val joinLink = bookRepo.generateJoinLink()
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, context.getString(R.string.menu_share_book, joinLink))
        }
        context.startActivity(Intent.createChooser(intent, context.getString(R.string.cta_invite)))
    }

    fun highlightInviteButton(dest: BubbleDest) {
        if (!isInviteBubbleShown) {
            isInviteBubbleShown = true
            bubbleRepo.addBubbleToQueue(dest)
        }
    }

    /**
     *  Show full screen Ad on every [fullScreenAdRecords] records
     */
    private val fullScreenAdRecords: Int get() = 5

    fun showFullScreenAdIfNeeded(context: Context) {
        val activity = context as? Activity ?: return

        if (recordCount % fullScreenAdRecords == 0 && !authManager.isPremium.value) {
            fullScreenAdsLoader.showAd(activity)
        }
    }

    private fun record() {
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
            name = note.value.trim().ifEmpty { category },
            price = calculator.price.value,
            author = authManager.userState.value?.toAuthor()
        )

        recordRepo.createRecord(record)
        _recordEvent.sendEvent()
        toaster.showMessage(context.getString(R.string.record_created, category))
        tracker.logEvent("record_created")
        recordCount += 1
        resetScreen()
    }

    private fun resetScreen() {
        _category.value = null
        _note.value = ""
        calculator.clearPrice()
    }
}
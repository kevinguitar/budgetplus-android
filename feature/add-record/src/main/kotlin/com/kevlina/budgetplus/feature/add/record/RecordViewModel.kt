package com.kevlina.budgetplus.feature.add.record

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kevlina.budgetplus.core.common.EventFlow
import com.kevlina.budgetplus.core.common.EventTrigger
import com.kevlina.budgetplus.core.common.MutableEventFlow
import com.kevlina.budgetplus.core.common.R
import com.kevlina.budgetplus.core.common.RecordType
import com.kevlina.budgetplus.core.common.StringProvider
import com.kevlina.budgetplus.core.common.consumeEach
import com.kevlina.budgetplus.core.common.sendEvent
import com.kevlina.budgetplus.core.common.withCurrentTime
import com.kevlina.budgetplus.core.data.AuthManager
import com.kevlina.budgetplus.core.data.BookRepo
import com.kevlina.budgetplus.core.data.FullScreenAdsLoader
import com.kevlina.budgetplus.core.data.RecordRepo
import com.kevlina.budgetplus.core.data.local.PreferenceHolder
import com.kevlina.budgetplus.core.data.remote.Record
import com.kevlina.budgetplus.core.data.remote.toAuthor
import com.kevlina.budgetplus.core.ui.SnackbarSender
import com.kevlina.budgetplus.core.ui.bubble.BubbleDest
import com.kevlina.budgetplus.core.ui.bubble.BubbleRepo
import com.kevlina.budgetplus.feature.category.pills.CategoriesViewModel
import com.kevlina.budgetplus.inapp.review.InAppReviewManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class RecordViewModel @Inject constructor(
    val calculatorVm: CalculatorViewModel,
    val categoriesVm: CategoriesViewModel,
    val bookRepo: BookRepo,
    private val recordRepo: RecordRepo,
    private val bubbleRepo: BubbleRepo,
    private val authManager: AuthManager,
    private val fullScreenAdsLoader: FullScreenAdsLoader,
    private val inAppReviewManager: InAppReviewManager,
    private val snackbarSender: SnackbarSender,
    private val stringProvider: StringProvider,
    preferenceHolder: PreferenceHolder,
) : ViewModel() {

    private val _type = MutableStateFlow(RecordType.Expense)
    val type: StateFlow<RecordType> = _type.asStateFlow()

    private val _recordDate = MutableStateFlow<RecordDateState>(RecordDateState.Now)
    val recordDate: StateFlow<RecordDateState> = _recordDate.asStateFlow()

    private val _note = MutableStateFlow("")
    val note: StateFlow<String> = _note.asStateFlow()

    val recordEvent = EventTrigger<Unit>()

    private val _requestReviewEvent = MutableEventFlow<Unit>()
    val requestReviewEvent: EventFlow<Unit> = _requestReviewEvent.asStateFlow()

    private val _requestPermissionEvent = MutableEventFlow<Unit>()
    val requestPermissionEvent: EventFlow<Unit> = _requestPermissionEvent.asStateFlow()

    private var isInviteBubbleShown by preferenceHolder.bindBoolean(false)
    private var recordCount by preferenceHolder.bindInt(0)

    init {
        calculatorVm.recordFlow
            .consumeEach(::record)
            .launchIn(viewModelScope)
    }

    fun setType(type: RecordType) {
        _type.value = type
    }

    fun setDate(date: LocalDate) {
        _recordDate.value = if (date.isEqual(LocalDate.now())) {
            RecordDateState.Now
        } else {
            RecordDateState.Other(date)
        }
    }

    fun setNote(note: String) {
        _note.value = note
    }

    fun shareJoinLink(context: Context) {
        val joinLink = bookRepo.generateJoinLink()
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, stringProvider[R.string.menu_invite_to_book, joinLink])
        }
        context.startActivity(Intent.createChooser(intent, stringProvider[R.string.cta_invite]))
        _requestPermissionEvent.sendEvent()
    }

    fun highlightInviteButton(dest: BubbleDest) {
        if (!isInviteBubbleShown) {
            isInviteBubbleShown = true
            bubbleRepo.addBubbleToQueue(dest)
        }
    }

    fun launchReviewFlow(activity: Activity) {
        viewModelScope.launch {
            inAppReviewManager.launchReviewFlow(activity)
        }
    }

    fun rejectReview() {
        inAppReviewManager.rejectReviewing()
    }

    fun showNotificationPermissionHint() {
        snackbarSender.send(R.string.permission_hint, canDismiss = true)
    }

    private fun record(context: Context) {
        val category = categoriesVm.category.value
        val price = calculatorVm.price.value

        if (category == null) {
            snackbarSender.send(message = R.string.record_empty_category)
            return
        }

        if (price == 0.0) {
            snackbarSender.send(R.string.record_empty_price)
            return
        }

        val record = Record(
            type = type.value,
            date = recordDate.value.date.toEpochDay(),
            timestamp = recordDate.value.date.withCurrentTime,
            category = category,
            name = note.value.trim().ifEmpty { category },
            price = calculatorVm.price.value,
            author = authManager.userState.value?.toAuthor()
        )

        recordRepo.createRecord(record)
        recordEvent.sendEvent(Unit)
        snackbarSender.send(stringProvider[R.string.record_created, category])
        recordCount += 1

        resetScreen()
        onRecordCreated(context)
    }

    private fun resetScreen() {
        categoriesVm.setCategory(null)
        _note.value = ""
        calculatorVm.clearPrice()
    }

    /**
     *  This callback does several things
     *  - Show full screen Ad on every [RECORD_COUNT_CYCLE] records
     *  - Request notification permission after the 2nd record
     *  - Request in app review after the 4th record
     */
    private fun onRecordCreated(context: Context) {
        val activity = context as? Activity ?: return
        when (recordCount % RECORD_COUNT_CYCLE) {
            RECORD_SHOW_AD -> fullScreenAdsLoader.showAd(activity)
            RECORD_REQUEST_PERMISSION -> _requestPermissionEvent.sendEvent()
            // Request the in app review when almost reach the next full screen ad,
            // just to have a better UX while user reviewing.
            RECORD_REQUEST_REVIEW -> if (inAppReviewManager.isEligibleForReview()) {
                _requestReviewEvent.sendEvent()
            }
        }
    }

    private companion object {
        /**
         *  Show full screen Ad on every [RECORD_COUNT_CYCLE] records
         */
        const val RECORD_COUNT_CYCLE = 7
        const val RECORD_SHOW_AD = 0
        const val RECORD_REQUEST_PERMISSION = 2
        const val RECORD_REQUEST_REVIEW = 4
    }
}
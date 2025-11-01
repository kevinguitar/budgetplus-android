package com.kevlina.budgetplus.feature.edit.category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kevlina.budgetplus.core.common.R
import com.kevlina.budgetplus.core.common.RecordType
import com.kevlina.budgetplus.core.common.SnackbarSender
import com.kevlina.budgetplus.core.common.StringProvider
import com.kevlina.budgetplus.core.data.BookRepo
import com.kevlina.budgetplus.core.data.CategoryRenameEvent
import com.kevlina.budgetplus.core.data.RecordRepo
import com.kevlina.budgetplus.core.ui.bubble.BubbleDest
import com.kevlina.budgetplus.core.ui.bubble.BubbleRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.annotations.VisibleForTesting
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class EditCategoryViewModel @Inject constructor(
    private val bookRepo: BookRepo,
    private val recordRepo: RecordRepo,
    private val bubbleRepo: BubbleRepo,
    private val snackbarSender: SnackbarSender,
    private val stringProvider: StringProvider,
) : ViewModel() {

    val expenseCategories
        get() = bookRepo.bookState.value?.expenseCategories.orEmpty()

    val incomeCategories
        get() = bookRepo.bookState.value?.incomeCategories.orEmpty()

    @VisibleForTesting
    val categoryRenameEvents = mutableListOf<CategoryRenameEvent>()

    private var saveBubbleJob: Job? = null

    fun updateCategories(type: RecordType, newCategories: List<String>) {
        bookRepo.updateCategories(type, newCategories)
        if (categoryRenameEvents.isNotEmpty()) {
            recordRepo.renameCategories(categoryRenameEvents)
        }
        snackbarSender.send(R.string.category_edit_successful)
    }

    fun highlightCategoryHint(dest: BubbleDest) {
        bubbleRepo.addBubbleToQueue(dest)
    }

    fun highlightSaveButton(dest: BubbleDest) {
        if (saveBubbleJob != null) return
        saveBubbleJob = viewModelScope.launch {
            // Display save hint after edit hint
            delay(1.seconds)
            bubbleRepo.addBubbleToQueue(dest)
        }
    }

    fun showCategoryExistError(category: String) {
        snackbarSender.send(stringProvider[R.string.category_already_exist, category])
    }

    fun onCategoryRenamed(oldName: String, newName: String) {
        if (oldName == newName) return

        val eventToReplace = categoryRenameEvents.find { it.to == oldName }
        if (eventToReplace == null) {
            categoryRenameEvents.add(CategoryRenameEvent(oldName, newName))
        } else {
            categoryRenameEvents.remove(eventToReplace)
            val newEvent = eventToReplace.copy(to = newName)
            if (newEvent.from != newEvent.to) {
                categoryRenameEvents.add(newEvent)
            }
        }
    }

    fun onCategoryDeleted(name: String) {
        categoryRenameEvents.removeIf { event ->
            event.to == name
        }
    }
}
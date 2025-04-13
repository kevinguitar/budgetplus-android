package com.kevlina.budgetplus.feature.settings

import android.annotation.SuppressLint
import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kevlina.budgetplus.core.common.R
import com.kevlina.budgetplus.core.common.SnackbarSender
import com.kevlina.budgetplus.core.common.StringProvider
import com.kevlina.budgetplus.core.common.Tracker
import com.kevlina.budgetplus.core.common.VibratorManager
import com.kevlina.budgetplus.core.common.combineState
import com.kevlina.budgetplus.core.common.mapState
import com.kevlina.budgetplus.core.data.AuthManager
import com.kevlina.budgetplus.core.data.BookRepo
import com.kevlina.budgetplus.core.settings.api.ChartModeViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class SettingsViewModel @Inject constructor(
    private val bookRepo: BookRepo,
    private val authManager: AuthManager,
    private val stringProvider: StringProvider,
    private val snackbarSender: SnackbarSender,
    private val tracker: Tracker,
    val vibrator: VibratorManager,
    val chartModel: ChartModeViewModel,
    val navigation: SettingsNavigationViewModel,
) : ViewModel() {

    val bookName = bookRepo.bookState.mapState { it?.name }
    val isBookOwner = bookRepo.bookState.combineState(
        other = authManager.userState,
        scope = viewModelScope
    ) { book, user ->
        book != null && book.ownerId == user?.id
    }

    val isPremium = authManager.isPremium

    @get:SuppressLint("AnnotateVersionCheck")
    val canSelectLanguage: Boolean
        get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU

    val isInsider = authManager.userState.mapState {
        it?.internal == true
    }

    val currentUsername get() = authManager.userState.value?.name
    val currentBookName get() = bookRepo.bookState.value?.name

    fun trackBatchRecordClicked() {
        tracker.logEvent("settings_batch_record_click")
    }

    fun renameUser(newName: String) {
        viewModelScope.launch {
            try {
                authManager.renameUser(newName)
                snackbarSender.send(stringProvider[R.string.settings_rename_user_success, newName])
            } catch (e: Exception) {
                snackbarSender.sendError(e)
            }
        }
    }

    fun renameBook(newName: String) {
        viewModelScope.launch {
            try {
                bookRepo.renameBook(newName)
                snackbarSender.send(stringProvider[R.string.settings_rename_book_success, newName])
            } catch (e: Exception) {
                snackbarSender.sendError(e)
            }
        }
    }

    fun deleteOrLeave() {
        viewModelScope.launch {
            val isBookOwner = isBookOwner.value
            val bookName = bookName.value
            try {
                bookRepo.leaveOrDeleteBook()
                snackbarSender.send(stringProvider[if (isBookOwner) {
                    R.string.settings_book_deleted
                } else {
                    R.string.settings_book_left
                }, bookName.orEmpty()])
            } catch (e: Exception) {
                snackbarSender.sendError(e)
            }
        }
    }
}
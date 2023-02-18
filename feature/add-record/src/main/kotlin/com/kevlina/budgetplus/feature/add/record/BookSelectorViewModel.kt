package com.kevlina.budgetplus.feature.add.record

import android.content.Context
import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kevlina.budgetplus.core.common.R
import com.kevlina.budgetplus.core.common.Toaster
import com.kevlina.budgetplus.core.common.Tracker
import com.kevlina.budgetplus.core.common.combineState
import com.kevlina.budgetplus.core.data.AuthManager
import com.kevlina.budgetplus.core.data.BookRepo
import com.kevlina.budgetplus.core.data.FREE_BOOKS_LIMIT
import com.kevlina.budgetplus.core.data.PREMIUM_BOOKS_LIMIT
import com.kevlina.budgetplus.core.data.remote.Book
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
@Stable
class BookSelectorViewModel @Inject constructor(
    private val bookRepo: BookRepo,
    private val toaster: Toaster,
    private val tracker: Tracker,
    authManager: AuthManager,
    @ApplicationContext private val context: Context,
) : ViewModel() {

    val book = bookRepo.bookState
    val books = bookRepo.booksState

    val createBookBtnState: StateFlow<CreateBookBtnState> = authManager.userState.combineState(
        other = bookRepo.booksState,
        scope = viewModelScope
    ) { user, books ->
        val isPremium = user?.premium == true
        val size = books.orEmpty().size
        when {
            isPremium && size >= PREMIUM_BOOKS_LIMIT -> CreateBookBtnState.ReachedMax
            !isPremium && size >= FREE_BOOKS_LIMIT -> CreateBookBtnState.NeedPremium
            else -> CreateBookBtnState.Enabled
        }
    }

    fun selectBook(book: Book) {
        bookRepo.selectBook(book)
    }

    fun createBook(name: String) {
        viewModelScope.launch {
            try {
                bookRepo.createBook(name)
                toaster.showMessage(context.getString(R.string.book_create_success, name))
                tracker.logEvent("book_created_from_selector")
            } catch (e: Exception) {
                toaster.showError(e)
            }
        }
    }

    fun showReachedMaxMessage() {
        toaster.showMessage(R.string.book_exceed_maximum)
    }

}
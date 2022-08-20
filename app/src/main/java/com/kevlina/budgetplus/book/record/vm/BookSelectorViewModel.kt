package com.kevlina.budgetplus.book.record.vm

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kevlina.budgetplus.R
import com.kevlina.budgetplus.auth.AuthManager
import com.kevlina.budgetplus.data.remote.Book
import com.kevlina.budgetplus.data.remote.BookRepo
import com.kevlina.budgetplus.data.remote.FREE_BOOKS_LIMIT
import com.kevlina.budgetplus.data.remote.PREMIUM_BOOKS_LIMIT
import com.kevlina.budgetplus.utils.Toaster
import com.kevlina.budgetplus.utils.Tracker
import com.kevlina.budgetplus.utils.combineState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
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

    fun buyPremium() {
        toaster.showMessage(R.string.premium_coming_soon)
        tracker.logEvent("buy_premium_attempt")
    }

    fun showReachedMaxMessage() {
        toaster.showMessage(R.string.book_exceed_maximum)
    }

}
package com.kevlina.budgetplus.feature.add.record

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kevlina.budgetplus.core.common.R
import com.kevlina.budgetplus.core.common.SnackbarSender
import com.kevlina.budgetplus.core.common.StringProvider
import com.kevlina.budgetplus.core.common.combineState
import com.kevlina.budgetplus.core.data.AuthManager
import com.kevlina.budgetplus.core.data.BookRepo
import com.kevlina.budgetplus.core.data.FREE_BOOKS_LIMIT
import com.kevlina.budgetplus.core.data.PREMIUM_BOOKS_LIMIT
import com.kevlina.budgetplus.core.data.remote.Book
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookSelectorViewModel @Inject constructor(
    private val bookRepo: BookRepo,
    private val snackbarSender: SnackbarSender,
    private val stringProvider: StringProvider,
    authManager: AuthManager,
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
                bookRepo.createBook(name = name, source = "selector")
                snackbarSender.send(stringProvider[R.string.book_create_success, name])
            } catch (e: Exception) {
                snackbarSender.sendError(e)
            }
        }
    }

    fun showReachedMaxMessage() {
        snackbarSender.send(R.string.book_exceed_maximum)
    }

}
package com.kevingt.moneybook.book.record.vm

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kevingt.moneybook.R
import com.kevingt.moneybook.auth.AuthManager
import com.kevingt.moneybook.data.remote.Book
import com.kevingt.moneybook.data.remote.BookRepo
import com.kevingt.moneybook.data.remote.FREE_BOOKS_LIMIT
import com.kevingt.moneybook.data.remote.PREMIUM_BOOKS_LIMIT
import com.kevingt.moneybook.utils.Toaster
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookSelectorViewModel @Inject constructor(
    private val bookRepo: BookRepo,
    private val toaster: Toaster,
    authManager: AuthManager,
    @ApplicationContext private val context: Context,
) : ViewModel() {

    val book = bookRepo.bookState
    val books = bookRepo.booksState

    val createBookBtnState: StateFlow<CreateBookBtnState> = combine(
        authManager.userState,
        bookRepo.booksState
    ) { user, books ->
        val isPremium = user?.premium == true
        val size = books.orEmpty().size
        when {
            isPremium && size >= PREMIUM_BOOKS_LIMIT -> CreateBookBtnState.ReachedMax
            !isPremium && size >= FREE_BOOKS_LIMIT -> CreateBookBtnState.NeedPremium
            else -> CreateBookBtnState.Enabled
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), CreateBookBtnState.Enabled)


    fun selectBook(book: Book) {
        bookRepo.selectBook(book)
    }

    fun createBook(name: String) {
        viewModelScope.launch {
            try {
                bookRepo.createBook(name)
                toaster.showMessage(context.getString(R.string.book_create_success, name))
            } catch (e: Exception) {
                toaster.showError(e)
            }
        }
    }

    fun buyPremium() {
        toaster.showMessage(R.string.premium_coming_soon)
    }

    fun showReachedMaxMessage() {
        toaster.showMessage(R.string.book_exceed_maximum)
    }

}
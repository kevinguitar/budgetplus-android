package com.kevingt.moneybook.book.record.vm

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kevingt.moneybook.R
import com.kevingt.moneybook.data.remote.Book
import com.kevingt.moneybook.data.remote.BookRepo
import com.kevingt.moneybook.utils.Toaster
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookSelectorViewModel @Inject constructor(
    private val bookRepo: BookRepo,
    private val toaster: Toaster,
    @ApplicationContext private val context: Context,
) : ViewModel() {

    val book = bookRepo.bookState
    val books = bookRepo.booksState

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

}
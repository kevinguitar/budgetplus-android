package com.kevingt.moneybook.book.record.vm

import androidx.lifecycle.ViewModel
import com.kevingt.moneybook.data.remote.Book
import com.kevingt.moneybook.data.remote.BookRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class BookSelectorViewModel @Inject constructor(
    private val bookRepo: BookRepo,
) : ViewModel() {

    val book = bookRepo.bookState
    val books = bookRepo.booksState

    fun selectBook(book: Book) {
        bookRepo.selectBook(book)
    }

}
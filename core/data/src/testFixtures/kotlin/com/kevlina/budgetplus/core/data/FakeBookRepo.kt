package com.kevlina.budgetplus.core.data

import com.kevlina.budgetplus.core.common.RecordType
import com.kevlina.budgetplus.core.data.remote.Book
import kotlinx.coroutines.flow.MutableStateFlow

class FakeBookRepo(
    book: Book? = null,
    books: List<Book>? = null,
    currentCurrencySymbol: String = "USD",
    override val currentBookId: String? = null,
    override val hasPendingJoinRequest: Boolean = false,
) : BookRepo {

    override val bookState = MutableStateFlow(book)
    override val booksState = MutableStateFlow(books)
    override val currencySymbol = MutableStateFlow(currentCurrencySymbol)

    override fun generateJoinLink(): String {
        error("Not yet implemented")
    }

    override fun setPendingJoinRequest(joinId: String?) {
        error("Not yet implemented")
    }

    override suspend fun handlePendingJoinRequest(): String? {
        error("Not yet implemented")
    }

    override suspend fun removeMember(userId: String) {
        error("Not yet implemented")
    }

    override suspend fun checkUserHasBook(): Boolean {
        error("Not yet implemented")
    }

    override suspend fun createBook(name: String, source: String) {
        error("Not yet implemented")
    }

    override suspend fun renameBook(newName: String) {
        error("Not yet implemented")
    }

    override suspend fun leaveOrDeleteBook() {
        error("Not yet implemented")
    }

    override fun selectBook(book: Book) {
        error("Not yet implemented")
    }

    override fun addCategory(type: RecordType, category: String, source: String) {
        error("Not yet implemented")
    }

    override fun formatPrice(price: Double, alwaysShowSymbol: Boolean): String {
        error("Not yet implemented")
    }

    override fun updateCategories(type: RecordType, categories: List<String>) {
        error("Not yet implemented")
    }

    override fun updateCurrency(currencyCode: String) {
        error("Not yet implemented")
    }
}
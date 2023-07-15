package com.kevlina.budgetplus.core.data

import com.kevlina.budgetplus.core.common.RecordType
import com.kevlina.budgetplus.core.data.remote.Book
import kotlinx.coroutines.flow.StateFlow

interface BookRepo {

    val bookState: StateFlow<Book?>
    val booksState: StateFlow<List<Book>?>

    val currentBookId: String?

    val hasPendingJoinRequest: Boolean

    fun generateJoinLink(): String

    fun setPendingJoinRequest(joinId: String?)

    /**
     *  @return The book's name if the user joined successfully.
     *  @throws JoinBookException
     */
    suspend fun handlePendingJoinRequest(): String?

    suspend fun removeMember(userId: String)

    suspend fun checkUserHasBook(): Boolean

    suspend fun createBook(name: String)

    suspend fun renameBook(newName: String)

    suspend fun leaveOrDeleteBook()

    fun selectBook(book: Book)

    fun updateCategories(type: RecordType, categories: List<String>)

}
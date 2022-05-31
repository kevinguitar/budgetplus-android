package com.kevingt.moneybook.data.remote

import android.net.Uri
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.kevingt.moneybook.auth.AuthManager
import com.kevingt.moneybook.data.local.PreferenceHolder
import com.kevingt.moneybook.data.local.bindObject
import com.kevingt.moneybook.utils.AppScope
import com.kevingt.moneybook.utils.await
import com.kevingt.moneybook.utils.requireValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

interface BookRepo {

    val bookIdState: StateFlow<String?>
    val bookState: StateFlow<Book?>

    val hasPendingJoinRequest: Boolean

    fun setPendingJoinRequest(uri: Uri)

    suspend fun handlePendingJoinRequest()

    suspend fun checkUserHasBook(): Boolean

    suspend fun createBook(name: String)

    /**
     *  Categories
     */
    fun addCategory(type: RecordType, category: String)

    fun editCategory(type: RecordType, oldCategory: String, newCategory: String)

    fun deleteCategory(type: RecordType, name: String)

}

@Singleton
class BookRepoImpl @Inject constructor(
    private val authManager: AuthManager,
    preferenceHolder: PreferenceHolder,
    @AppScope appScope: CoroutineScope,
) : BookRepo {

    private var currentBookId by preferenceHolder.bindString(null)
    private var currentBook by preferenceHolder.bindObject<Book>(null)

    private val _bookIdState = MutableStateFlow(currentBookId)
    override val bookIdState: StateFlow<String?> get() = _bookIdState

    private val _bookState = MutableStateFlow(currentBook)
    override val bookState: StateFlow<Book?> get() = _bookState

    override val hasPendingJoinRequest: Boolean
        get() = pendingJoinId.value != null

    private val pendingJoinId = MutableStateFlow<String?>(null)

    private val booksDb = Firebase.firestore.collection("books")

    private val authorsField get() = "authors"

    init {
        authManager.userState
            .onEach(::onUserChanged)
            .launchIn(appScope)
    }

    override fun setPendingJoinRequest(uri: Uri) {
        val bookId = uri.lastPathSegment
        pendingJoinId.value = bookId
    }

    override suspend fun handlePendingJoinRequest() {
        val bookId = requireNotNull(pendingJoinId.value) { "Doesn't have pending join request" }
        val user = requireNotNull(authManager.userState.value) { "User is null" }
        val author = user.toAuthor()

        val book = booksDb.document(bookId).get().requireValue<Book>()
        val newAuthors = book.authors.toMutableList()
        if (author !in newAuthors) {
            newAuthors.add(author)
        }

        booksDb.document(bookId)
            .update(authorsField, newAuthors)
            .await()

        pendingJoinId.value = null
        observeBook(bookId)
    }

    override suspend fun checkUserHasBook(): Boolean {
        val user = authManager.requireUser()
        return getBookId(user.toAuthor()) != null
    }

    override suspend fun createBook(name: String) {
        val user = requireNotNull(authManager.userState.value) { "User is null" }
        val newBook = Book(
            name = name,
            authors = listOf(user.toAuthor()),
            expenseCategories = listOf("Food", "Daily", "Transport"),
            incomeCategories = listOf("Salary")
        )
        val doc = booksDb.add(newBook).await()
        observeBook(doc.id)
    }

    override fun addCategory(type: RecordType, category: String) {
        val book = bookState.value ?: return
        val newCategories = when (type) {
            RecordType.Expense -> book.expenseCategories
            RecordType.Income -> book.incomeCategories
        }
            .toMutableList()
            .apply { add(category) }

        updateCategories(type, newCategories)
    }

    override fun editCategory(type: RecordType, oldCategory: String, newCategory: String) {
        val book = bookState.value ?: return
        val newCategories = when (type) {
            RecordType.Expense -> book.expenseCategories
            RecordType.Income -> book.incomeCategories
        }.toMutableList()

        val index = newCategories.indexOf(oldCategory)
        if (index == -1) return

        newCategories[index] = newCategory

        updateCategories(type, newCategories)
    }

    override fun deleteCategory(type: RecordType, name: String) {
        val book = bookState.value ?: return
        val newCategories = when (type) {
            RecordType.Expense -> book.expenseCategories
            RecordType.Income -> book.incomeCategories
        }
            .toMutableList()
            .apply { remove(name) }

        updateCategories(type, newCategories)
    }

    private fun updateCategories(type: RecordType, categories: List<String>) {
        val bookId = bookIdState.value ?: return
        booksDb.document(bookId)
            .update(
                when (type) {
                    RecordType.Expense -> "expenseCategories"
                    RecordType.Income -> "incomeCategories"
                },
                categories
            )
    }

    private var bookRegistration: ListenerRegistration? = null

    private suspend fun onUserChanged(user: User?) {
        if (user == null) {
            setBookId(null)
            setBook(null)
            bookRegistration?.remove()
        } else {
            val bookId = getBookId(user.toAuthor()) ?: return
            observeBook(bookId)
        }
    }

    private fun observeBook(bookId: String) {
        setBookId(bookId)

        bookRegistration?.remove()
        bookRegistration = booksDb.document(bookId)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Timber.e(e, "Listen failed.")
                    return@addSnapshotListener
                }

                if (snapshot == null) {
                    Timber.d("BookRepo: Snapshot is empty")
                    return@addSnapshotListener
                }

                setBook(snapshot.toObject<Book>())
            }
    }

    private suspend fun getBookId(author: Author): String? {
        val snapshot = booksDb.whereArrayContains(authorsField, author).get().await()
        return snapshot.firstOrNull()?.id
    }

    private fun setBookId(bookId: String?) {
        _bookIdState.value = bookId
        currentBookId = bookId
    }

    private fun setBook(book: Book?) {
        _bookState.value = book
        currentBook = book
    }
}
package com.kevingt.moneybook.data.remote

import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.Source
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.kevingt.moneybook.R
import com.kevingt.moneybook.auth.AuthManager
import com.kevingt.moneybook.data.local.PreferenceHolder
import com.kevingt.moneybook.utils.AppScope
import com.kevingt.moneybook.utils.await
import com.kevingt.moneybook.utils.requireValue
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration.Companion.days

interface BookRepo {

    val bookState: StateFlow<Book?>
    val booksState: StateFlow<List<Book>?>

    val currentBookId: String?

    val hasPendingJoinRequest: Boolean

    fun generateJoinLink(): String

    fun setPendingJoinRequest(uri: Uri)

    /**
     *  @return The book's name that the user just joined.
     *  @throws JoinLinkExpiredException
     *  @throws ExceedFreeBooksLimitException
     *  @throws ExceedPremiumBooksLimitException
     */
    suspend fun handlePendingJoinRequest(): String

    suspend fun removeMember(userId: String)

    suspend fun checkUserHasBook(): Boolean

    suspend fun createBook(name: String)

    suspend fun renameBook(newName: String)

    suspend fun leaveOrDeleteBook()

    fun selectBook(book: Book)

    fun updateCategories(type: RecordType, categories: List<String>)

}

@Singleton
class BookRepoImpl @Inject constructor(
    private val authManager: AuthManager,
    preferenceHolder: PreferenceHolder,
    @AppScope appScope: CoroutineScope,
    @ApplicationContext private val context: Context,
) : BookRepo {

    private var currentBook by preferenceHolder.bindObjectOptional<Book>(null)

    private val _bookState = MutableStateFlow(currentBook)
    override val bookState: StateFlow<Book?> = _bookState.asStateFlow()

    private val _booksState = MutableStateFlow<List<Book>?>(null)
    override val booksState: StateFlow<List<Book>?> = _booksState.asStateFlow()

    override val currentBookId: String? get() = bookState.value?.id
    private val requireBookId: String get() = requireNotNull(currentBookId) { "Book id is null." }

    override val hasPendingJoinRequest: Boolean
        get() = pendingJoinUri.value != null

    private val pendingJoinUri = MutableStateFlow<Uri?>(null)

    private val booksDb = Firebase.firestore.collection("books")

    private val authorsField get() = "authors"
    private val createdOnField get() = "createdOn"
    private val archivedField get() = "archived"

    init {
        authManager.userState
            .onEach(::onUserChanged)
            .launchIn(appScope)
    }

    override fun generateJoinLink(): String {
        // The join link will expire in 1 day
        val validBefore = System.currentTimeMillis() + 1.days.inWholeMilliseconds
        val joinLink = "https://moneybook.cchi.tw/".toUri()
            .buildUpon()
            .appendPath("join")
            .appendPath(requireBookId)
            .appendQueryParameter("valid", validBefore.toString())
            .build()
        return joinLink.toString()
    }

    override fun setPendingJoinRequest(uri: Uri) {
        pendingJoinUri.value = uri
    }

    private suspend fun getLatestBook(bookId: String): Book {
        return booksDb.document(bookId).get(Source.SERVER)
            .requireValue<Book>()
            .copy(id = bookId)
    }

    override suspend fun handlePendingJoinRequest(): String {
        val isPremium = authManager.userState.value?.premium == true
        val bookCount = booksState.value.orEmpty().size
        when {
            isPremium && bookCount >= PREMIUM_BOOKS_LIMIT -> throw ExceedPremiumBooksLimitException()
            !isPremium && bookCount >= FREE_BOOKS_LIMIT -> throw ExceedFreeBooksLimitException()
        }

        val uri = requireNotNull(pendingJoinUri.value) { "Doesn't have pending join request" }
        val bookId = uri.lastPathSegment ?: error("No book id is presented")
        val validBefore = uri.getQueryParameter("valid")
        if (validBefore == null || System.currentTimeMillis() > validBefore.toLong()) {
            throw JoinLinkExpiredException()
        }

        val userId = authManager.requireUserId()

        val book = getLatestBook(bookId)
        val newAuthors = book.authors.toMutableList()
        if (userId !in newAuthors) {
            newAuthors.add(userId)
        }

        setBook(book)
        booksDb.document(bookId)
            .update(authorsField, newAuthors)
            .await()

        pendingJoinUri.value = null
        return book.name
    }

    override suspend fun removeMember(userId: String) {
        val book = getLatestBook(requireBookId)
        val newAuthors = book.authors.toMutableList()
        if (userId in newAuthors) {
            newAuthors.remove(userId)
        }

        booksDb.document(requireBookId)
            .update(authorsField, newAuthors)
            .await()
    }

    override suspend fun checkUserHasBook(): Boolean {
        val userId = authManager.requireUserId()
        val books = booksDb
            .whereArrayContains(authorsField, userId)
            .whereEqualTo(archivedField, false)
            .get()
            .await()
        return !books.isEmpty
    }

    override suspend fun createBook(name: String) {
        val userId = authManager.requireUserId()
        val expenses = context.resources.getStringArray(R.array.default_expense_categories)
        val incomes = context.resources.getStringArray(R.array.default_income_categories)
        val newBook = Book(
            name = name,
            ownerId = userId,
            authors = listOf(userId),
            expenseCategories = expenses.toList(),
            incomeCategories = incomes.toList()
        )
        val doc = booksDb.add(newBook).await()
        setBook(newBook.copy(id = doc.id))
    }

    override suspend fun renameBook(newName: String) {
        booksDb.document(requireBookId).update("name", newName).await()
    }

    override suspend fun leaveOrDeleteBook() {
        val book = bookState.value ?: return
        if (book.ownerId == authManager.requireUserId()) {
            booksDb.document(book.id)
                .update(archivedField, true)
                .await()
        } else {
            val authorsWithoutMe = book.authors.toMutableList()
            authorsWithoutMe.remove(authManager.requireUserId())

            booksDb.document(book.id)
                .update(authorsField, authorsWithoutMe)
                .await()
        }
    }

    override fun selectBook(book: Book) {
        setBook(book)
    }

    private var bookRegistration: ListenerRegistration? = null

    private fun onUserChanged(user: User?) {
        if (user == null) {
            _booksState.value = null
            bookRegistration?.remove()
        } else {
            observeBooks(user.id)
        }
    }

    private fun observeBooks(userId: String) {
        bookRegistration?.remove()
        bookRegistration = booksDb
            .whereArrayContains(authorsField, userId)
            .whereEqualTo(archivedField, false)
            .orderBy(createdOnField, Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Timber.e(e, "Listen failed.")
                    return@addSnapshotListener
                }

                if (snapshot == null) {
                    Timber.d("BookRepo: Snapshot is empty")
                    return@addSnapshotListener
                }

                val books = snapshot.map { doc -> doc.toObject<Book>().copy(id = doc.id) }
                _booksState.value = books

                if (books.isEmpty()) {
                    setBook(null)
                    return@addSnapshotListener
                }

                val bookId = currentBookId
                val index = books.indexOfFirst { it.id == bookId }
                if (bookId == null || index == -1) {
                    setBook(books.last())
                } else {
                    setBook(books[index])
                }
            }
    }

    private fun setBook(book: Book?) {
        _bookState.value = book
        currentBook = book
    }

    override fun updateCategories(type: RecordType, categories: List<String>) {
        booksDb.document(requireBookId)
            .update(
                when (type) {
                    RecordType.Expense -> "expenseCategories"
                    RecordType.Income -> "incomeCategories"
                },
                categories
            )
    }
}
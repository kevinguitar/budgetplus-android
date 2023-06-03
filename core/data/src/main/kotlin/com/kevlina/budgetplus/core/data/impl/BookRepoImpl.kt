package com.kevlina.budgetplus.core.data.impl

import androidx.core.net.toUri
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.Source
import com.google.firebase.firestore.ktx.toObject
import com.kevlina.budgetplus.core.common.AppScope
import com.kevlina.budgetplus.core.common.R
import com.kevlina.budgetplus.core.common.RecordType
import com.kevlina.budgetplus.core.common.StringProvider
import com.kevlina.budgetplus.core.common.Tracker
import com.kevlina.budgetplus.core.common.nav.APP_DEEPLINK
import com.kevlina.budgetplus.core.data.AuthManager
import com.kevlina.budgetplus.core.data.BookRepo
import com.kevlina.budgetplus.core.data.FREE_BOOKS_LIMIT
import com.kevlina.budgetplus.core.data.JoinBookException
import com.kevlina.budgetplus.core.data.JoinInfoProcessor
import com.kevlina.budgetplus.core.data.PREMIUM_BOOKS_LIMIT
import com.kevlina.budgetplus.core.data.local.PreferenceHolder
import com.kevlina.budgetplus.core.data.remote.Book
import com.kevlina.budgetplus.core.data.remote.BooksDb
import com.kevlina.budgetplus.core.data.requireValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration.Companion.days

@Singleton
internal class BookRepoImpl @Inject constructor(
    private val authManager: AuthManager,
    private val joinInfoProcessor: JoinInfoProcessor,
    private val stringProvider: StringProvider,
    private val tracker: Tracker,
    preferenceHolder: PreferenceHolder,
    @AppScope appScope: CoroutineScope,
    @BooksDb private val booksDb: dagger.Lazy<CollectionReference>,
) : BookRepo {

    private var currentBook by preferenceHolder.bindObjectOptional<Book>(null)

    private val _bookState = MutableStateFlow(currentBook)
    override val bookState: StateFlow<Book?> = _bookState.asStateFlow()

    private val _booksState = MutableStateFlow<List<Book>?>(null)
    override val booksState: StateFlow<List<Book>?> = _booksState.asStateFlow()

    override val currentBookId: String? get() = bookState.value?.id
    private val requireBookId: String get() = requireNotNull(currentBookId) { "Book id is null." }

    override val hasPendingJoinRequest: Boolean
        get() = pendingJoinId.value != null

    private val pendingJoinId = MutableStateFlow<String?>(null)

    private val authorsField get() = "authors"
    private val createdOnField get() = "createdOn"
    private val archivedField get() = "archived"

    init {
        authManager.userState
            .map { it?.id }
            .distinctUntilChanged()
            .onEach(::observeBooks)
            .launchIn(appScope)
    }

    override fun generateJoinLink(): String {
        val joinId = joinInfoProcessor.generateJoinId(requireBookId)
        val joinLink = APP_DEEPLINK.toUri()
            .buildUpon()
            .appendPath("join")
            .appendPath(joinId)
            .build()
        tracker.logEvent("join_book_link_generated")
        return joinLink.toString()
    }

    override fun setPendingJoinRequest(joinId: String?) {
        pendingJoinId.value = joinId
    }

    private suspend fun getLatestBook(bookId: String): Book {
        return booksDb.get().document(bookId).get(Source.SERVER)
            .requireValue<Book>()
            .copy(id = bookId)
    }

    // The join link will expire in 1 day
    private val linkExpirationMillis get() = 1.days.inWholeMilliseconds

    override suspend fun handlePendingJoinRequest(): String {
        val joinId = requireNotNull(pendingJoinId.value) { "Doesn't have pending join request" }
        val joinInfo = joinInfoProcessor.resolveJoinId(joinId)
        val bookId = joinInfo.bookId
        val validBefore = joinInfo.generatedOn + linkExpirationMillis
        pendingJoinId.value = null

        val userId = authManager.requireUserId()
        val isPremium = authManager.userState.value?.premium == true
        val bookCount = booksState.filterNotNull().first().size
        val book = getLatestBook(bookId)

        when {
            book.archived -> throw JoinBookException.General(R.string.book_already_archived)
            userId in book.authors -> throw JoinBookException.General(R.string.book_already_joined)
            isPremium && bookCount >= PREMIUM_BOOKS_LIMIT -> {
                tracker.logEvent("join_book_reach_max_limit")
                throw JoinBookException.General(R.string.book_exceed_maximum)
            }

            !isPremium && bookCount >= FREE_BOOKS_LIMIT -> {
                tracker.logEvent("join_book_reach_free_limit")
                throw JoinBookException.ExceedFreeLimit(R.string.book_join_exceed_free_limit)
            }

            System.currentTimeMillis() > validBefore -> {
                tracker.logEvent("join_book_link_expired")
                throw JoinBookException.General(R.string.book_join_link_expired)
            }
        }

        val newAuthors = book.authors.toMutableList()
        newAuthors.add(userId)

        setBook(book)
        booksDb.get().document(bookId)
            .update(authorsField, newAuthors)
            .await()

        tracker.logEvent("join_book_success")
        return book.name
    }

    override suspend fun removeMember(userId: String) {
        val book = getLatestBook(requireBookId)
        val newAuthors = book.authors.toMutableList()
        if (userId in newAuthors) {
            newAuthors.remove(userId)
        }

        booksDb.get().document(requireBookId)
            .update(authorsField, newAuthors)
            .await()
    }

    override suspend fun checkUserHasBook(): Boolean {
        val userId = authManager.requireUserId()
        val books = booksDb.get()
            .whereArrayContains(authorsField, userId)
            .whereEqualTo(archivedField, false)
            .get()
            .await()
        return !books.isEmpty
    }

    override suspend fun createBook(name: String) {
        val isPremium = authManager.userState.value?.premium == true
        val bookCount = booksState.filterNotNull().first().size

        val exceedFreeLimit = !isPremium && bookCount >= FREE_BOOKS_LIMIT
        val exceedPremiumLimit = isPremium && bookCount >= PREMIUM_BOOKS_LIMIT
        if (exceedFreeLimit || exceedPremiumLimit) {
            error(stringProvider[R.string.book_exceed_maximum])
        }

        val userId = authManager.requireUserId()
        val expenses = stringProvider.getArray(R.array.default_expense_categories)
        val incomes = stringProvider.getArray(R.array.default_income_categories)
        val newBook = Book(
            name = name,
            ownerId = userId,
            authors = listOf(userId),
            expenseCategories = expenses.toList(),
            incomeCategories = incomes.toList()
        )
        val doc = booksDb.get().add(newBook).await()
        setBook(newBook.copy(id = doc.id))
    }

    override suspend fun renameBook(newName: String) {
        booksDb.get().document(requireBookId).update("name", newName).await()
        tracker.logEvent("book_renamed")
    }

    override suspend fun leaveOrDeleteBook() {
        val book = bookState.value ?: return
        if (book.ownerId == authManager.requireUserId()) {
            booksDb.get().document(book.id)
                .update(archivedField, true)
                .await()
            tracker.logEvent("book_deleted")
        } else {
            val authorsWithoutMe = book.authors.toMutableList()
            authorsWithoutMe.remove(authManager.requireUserId())

            booksDb.get().document(book.id)
                .update(authorsField, authorsWithoutMe)
                .await()
            tracker.logEvent("book_leaved")
        }
    }

    override fun selectBook(book: Book) {
        setBook(book)
    }

    private var bookRegistration: ListenerRegistration? = null

    private fun observeBooks(userId: String?) {
        if (userId == null) {
            _booksState.value = null
            bookRegistration?.remove()
            return
        }

        bookRegistration?.remove()
        bookRegistration = booksDb.get()
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
        booksDb.get().document(requireBookId)
            .update(
                when (type) {
                    RecordType.Expense -> "expenseCategories"
                    RecordType.Income -> "incomeCategories"
                },
                categories
            )
    }
}
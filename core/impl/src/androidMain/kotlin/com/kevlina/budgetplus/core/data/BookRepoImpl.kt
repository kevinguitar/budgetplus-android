package com.kevlina.budgetplus.core.data

import androidx.core.net.toUri
import androidx.datastore.preferences.core.stringPreferencesKey
import budgetplus.core.common.generated.resources.Res
import budgetplus.core.common.generated.resources.book_already_archived
import budgetplus.core.common.generated.resources.book_already_joined
import budgetplus.core.common.generated.resources.book_exceed_maximum
import budgetplus.core.common.generated.resources.book_join_exceed_free_limit
import budgetplus.core.common.generated.resources.book_join_link_expired
import budgetplus.core.common.generated.resources.default_expense_categories
import budgetplus.core.common.generated.resources.default_income_categories
import co.touchlab.kermit.Logger
import com.kevlina.budgetplus.core.common.AppCoroutineScope
import com.kevlina.budgetplus.core.common.AppStartAction
import com.kevlina.budgetplus.core.common.RecordType
import com.kevlina.budgetplus.core.common.Tracker
import com.kevlina.budgetplus.core.common.mapState
import com.kevlina.budgetplus.core.common.nav.APP_DEEPLINK
import com.kevlina.budgetplus.core.common.nav.NAV_JOIN_PATH
import com.kevlina.budgetplus.core.data.local.Preference
import com.kevlina.budgetplus.core.data.remote.Book
import com.kevlina.budgetplus.core.data.remote.BooksDb
import dev.gitlive.firebase.firestore.CollectionReference
import dev.gitlive.firebase.firestore.Direction
import dev.gitlive.firebase.firestore.Source
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.ContributesIntoSet
import dev.zacsweers.metro.SingleIn
import dev.zacsweers.metro.binding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.getStringArray
import java.math.RoundingMode
import java.text.NumberFormat
import java.util.*
import kotlin.time.Clock
import kotlin.time.Duration.Companion.days

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class, binding = binding<BookRepo>())
@ContributesIntoSet(AppScope::class, binding = binding<AppStartAction>())
class BookRepoImpl(
    private val authManager: AuthManager,
    private val joinInfoProcessor: JoinInfoProcessor,
    private val tracker: Tracker,
    private val preference: Preference,
    @AppCoroutineScope private val appScope: CoroutineScope,
    @BooksDb private val booksDb: Lazy<CollectionReference>,
) : BookRepo, AppStartAction {

    private val currentBookKey = stringPreferencesKey("currentBook")
    private val currentBookFlow = preference.of(currentBookKey, Book.serializer())

    override val bookState: StateFlow<Book?> = runBlocking {
        currentBookFlow.stateIn(
            scope = appScope,
            started = SharingStarted.Eagerly,
            initialValue = currentBookFlow.first()
        )
    }

    final override val booksState: StateFlow<List<Book>?>
        field = MutableStateFlow<List<Book>?>(null)

    override val currentBookId: String? get() = bookState.value?.id
    private val requireBookId: String get() = requireNotNull(currentBookId) { "Book id is null." }

    private val currency: StateFlow<Currency> = bookState.mapState { book ->
        val code = book?.currencyCode
        if (code == null) {
            defaultCurrency
        } else {
            try {
                Currency.getInstance(code)
            } catch (e: Exception) {
                Logger.e(e) { "Failed to parse currency. $code" }
                defaultCurrency
            }
        }
    }

    override val currencySymbol: StateFlow<String> = currency.mapState {
        it.getSymbol(Locale.getDefault())
    }

    private val defaultCurrency: Currency
        get() = Currency.getInstance(Locale.getDefault())

    private val pricingFormat by lazy {
        NumberFormat.getCurrencyInstance()
            .also {
                it.roundingMode = RoundingMode.HALF_UP
                it.minimumFractionDigits = 0
                it.maximumFractionDigits = 2
            }
    }

    override val canEdit: Boolean
        get() = bookState.value?.allowMembersEdit != false ||
            bookState.value?.ownerId == authManager.userId

    override val hasPendingJoinRequest: Boolean
        get() = pendingJoinId.value != null

    private val pendingJoinId = MutableStateFlow<String?>(null)

    private val authorsField get() = "authors"
    private val createdOnField get() = "createdOn"
    private val archivedField get() = "archived"
    private val archivedOnField get() = "archivedOn"
    private val allowMembersEditField get() = "allowMembersEdit"

    // The join link will expire in 1 day
    private val linkExpirationMillis get() = 1.days.inWholeMilliseconds

    private var bookJob: Job? = null

    init {
        // Activate book flow throughout the entire app process
        appScope.launch { bookState.collect() }

        authManager.userState
            .map { it?.id }
            .distinctUntilChanged()
            .onEach(::observeBooks)
            .launchIn(appScope)
    }

    override fun onAppStart() {
        // Crucial to load the current book from storage on app start
        bookState.value
    }

    override suspend fun generateJoinLink(): String {
        val joinId = joinInfoProcessor.generateJoinId(requireBookId)
        val joinLink = APP_DEEPLINK.toUri()
            .buildUpon()
            .appendPath(NAV_JOIN_PATH)
            .appendPath(joinId)
            .build()
        tracker.logEvent("join_book_link_generated")
        return joinLink.toString()
    }

    override fun setPendingJoinRequest(joinId: String?) {
        pendingJoinId.value = joinId
    }

    private suspend fun getLatestBook(bookId: String): Book {
        return booksDb.value.document(bookId).get(Source.SERVER)
            .data<Book>()
            .copy(id = bookId)
    }

    override suspend fun handlePendingJoinRequest(): String? {
        val joinId = requireNotNull(pendingJoinId.value) { "Doesn't have pending join request" }
        pendingJoinId.value = null

        val joinInfo = joinInfoProcessor.resolveJoinId(joinId) ?: return null
        val bookId = joinInfo.bookId
        val validBefore = joinInfo.generatedOn + linkExpirationMillis

        val userId = authManager.requireUserId()
        val isPremium = authManager.userState.value?.premium == true
        val bookCount = booksState.filterNotNull().first().size
        val book = getLatestBook(bookId)

        when {
            book.archived -> throw JoinBookException.General(getString(Res.string.book_already_archived))
            userId in book.authors -> throw JoinBookException.General(getString(Res.string.book_already_joined))
            isPremium && bookCount >= PREMIUM_BOOKS_LIMIT -> {
                tracker.logEvent("join_book_reach_max_limit")
                throw JoinBookException.General(getString(Res.string.book_exceed_maximum))
            }

            !isPremium && bookCount >= FREE_BOOKS_LIMIT -> {
                tracker.logEvent("join_book_reach_free_limit")
                throw JoinBookException.ExceedFreeLimit(getString(Res.string.book_join_exceed_free_limit))
            }

            Clock.System.now().toEpochMilliseconds() > validBefore -> {
                tracker.logEvent("join_book_link_expired")
                throw JoinBookException.General(getString(Res.string.book_join_link_expired))
            }
        }

        val newAuthors = book.authors.toMutableList()
        newAuthors.add(userId)

        selectBook(book)
        booksDb.value.document(bookId)
            .updateFields { authorsField to newAuthors }

        tracker.logEvent("join_book_success")
        return book.name
    }

    override suspend fun removeMember(userId: String) {
        val book = getLatestBook(requireBookId)
        val newAuthors = book.authors.toMutableList()
        if (userId in newAuthors) {
            newAuthors.remove(userId)
        }

        booksDb.value.document(requireBookId)
            .updateFields { authorsField to newAuthors }
        tracker.logEvent("member_removed")
    }

    override suspend fun checkUserHasBook(): Boolean {
        Logger.d { "DEBUGG: Waiting for user id" }
        val userId = authManager.userState.filterNotNull().first().id
        Logger.d { "DEBUGG: user id $userId" }
        val books = booksDb.value
            .where { authorsField contains userId }
            .where { archivedField equalTo false }
            .orderBy(createdOnField, Direction.ASCENDING)
            .get()
        Logger.d { "DEBUGG: User has ${books.documents.size} books" }
        return books.documents.isNotEmpty()
    }

    override suspend fun createBook(name: String, source: String) {
        val isPremium = authManager.userState.value?.premium == true
        val bookCount = booksState.filterNotNull().first().size

        val exceedFreeLimit = !isPremium && bookCount >= FREE_BOOKS_LIMIT
        val exceedPremiumLimit = isPremium && bookCount >= PREMIUM_BOOKS_LIMIT
        if (exceedFreeLimit || exceedPremiumLimit) {
            error(getString(Res.string.book_exceed_maximum))
        }

        val userId = authManager.requireUserId()
        val expenses = getStringArray(Res.array.default_expense_categories)
        val incomes = getStringArray(Res.array.default_income_categories)
        val newBook = Book(
            name = name,
            ownerId = userId,
            authors = listOf(userId),
            expenseCategories = expenses.toList(),
            incomeCategories = incomes.toList(),
            currencyCode = defaultCurrency.currencyCode
        )
        val doc = booksDb.value.add(newBook)
        selectBook(newBook.copy(id = doc.id))
        tracker.logEvent("book_created_from_$source")
    }

    override suspend fun renameBook(newName: String) {
        booksDb.value.document(requireBookId)
            .updateFields { "name" to newName }
        tracker.logEvent("book_renamed")
    }

    override suspend fun leaveOrDeleteBook() {
        val book = bookState.value ?: return
        if (book.ownerId == authManager.requireUserId()) {
            booksDb.value.document(book.id)
                .updateFields {
                    archivedField to true
                    archivedOnField to Clock.System.now().toEpochMilliseconds()
                }
            tracker.logEvent("book_deleted")
        } else {
            val authorsWithoutMe = book.authors.toMutableList()
            authorsWithoutMe.remove(authManager.requireUserId())

            booksDb.value.document(book.id)
                .updateFields {
                    authorsField to authorsWithoutMe
                }
            tracker.logEvent("book_leaved")
        }
    }

    override suspend fun selectBook(book: Book?) {
        if (book == null) {
            preference.remove(currentBookKey)
        } else {
            preference.update(currentBookKey, Book.serializer(), book)
        }
    }

    private fun observeBooks(userId: String?) {
        if (userId == null) {
            booksState.value = null
            bookJob?.cancel()
            return
        }

        bookJob?.cancel()
        bookJob = booksDb.value
            .where { authorsField contains userId }
            .where { archivedField equalTo false }
            .orderBy(createdOnField, Direction.ASCENDING)
            .snapshots
            .catch { Logger.e(it) { "BookRepo: Listen failed." } }
            .onEach { snapshot ->
                val books = snapshot.documents.map { doc -> doc.data<Book>().copy(id = doc.id) }
                booksState.value = books

                if (books.isEmpty()) {
                    selectBook(null)
                    return@onEach
                }

                val bookId = currentBookId
                val index = books.indexOfFirst { it.id == bookId }
                if (bookId == null || index == -1) {
                    selectBook(books.last())
                } else {
                    selectBook(books[index])
                }
            }
            .launchIn(appScope)
    }

    override suspend fun addCategory(type: RecordType, category: String, source: String) {
        val book = bookState.value ?: return
        val currentCategories = when (type) {
            RecordType.Expense -> book.expenseCategories
            RecordType.Income -> book.incomeCategories
        }
        updateCategories(type, currentCategories + category)
        tracker.logEvent("categories_added_from_$source")
    }

    override fun formatPrice(price: Double, alwaysShowSymbol: Boolean): String {
        val bookCurrency = currency.value
        return if (alwaysShowSymbol || bookCurrency.getSymbol(Locale.getDefault()).length == 1) {
            pricingFormat.currency = bookCurrency
            pricingFormat.format(price)
        } else {
            price.plainPriceString
        }
    }

    override suspend fun updateCategories(type: RecordType, categories: List<String>) {
        val field = when (type) {
            RecordType.Expense -> "expenseCategories"
            RecordType.Income -> "incomeCategories"
        }
        booksDb.value
            .document(requireBookId)
            .updateFields { field to categories }
        tracker.logEvent("categories_updated")
    }

    override suspend fun updateCurrency(currencyCode: String) {
        booksDb.value
            .document(requireBookId)
            .updateFields { "currencyCode" to currencyCode }

        tracker.logEvent(
            event = "currency_updated",
            params = mapOf("currency_code" to currencyCode)
        )
    }

    override suspend fun setAllowMembersEdit(allow: Boolean) {
        booksDb.value
            .document(requireBookId)
            .updateFields { allowMembersEditField to allow }

        tracker.logEvent(
            event = "allow_members_edit_updated",
            params = mapOf("allow" to allow)
        )
    }
}
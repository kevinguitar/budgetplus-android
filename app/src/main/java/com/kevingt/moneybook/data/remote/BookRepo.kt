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
import kotlinx.coroutines.flow.*
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

    init {
        authManager.userState
            .map { it?.id }
            .distinctUntilChanged()
            .onEach(::onUserIdChanged)
            .launchIn(appScope)
    }

    override fun setPendingJoinRequest(uri: Uri) {
        val bookId = uri.lastPathSegment
        pendingJoinId.value = bookId
    }

    override suspend fun handlePendingJoinRequest() {
        val bookId = requireNotNull(pendingJoinId.value) { "Doesn't have pending join request" }
        val userId = authManager.requireUserId()
        val book = booksDb.document(bookId).get().requireValue<Book>()
        val newUsers = book.users.toMutableList()
        if (userId !in newUsers) {
            newUsers.add(userId)
        }

        booksDb.document(bookId)
            .update("users", newUsers)
            .await()

        pendingJoinId.value = null
        observeBook(bookId)
    }

    override suspend fun checkUserHasBook(): Boolean {
        val userId = authManager.requireUserId()
        return getBookId(userId) != null
    }

    override suspend fun createBook(name: String) {
        val userId = authManager.requireUserId()
        val newBook = Book(
            name = name,
            users = listOf(userId),
            expenseCategories = listOf("Food", "Daily", "Transport"),
            incomeCategories = listOf("Salary")
        )
        val doc = booksDb.add(newBook).await()
        observeBook(doc.id)
    }

    private var bookRegistration: ListenerRegistration? = null

    private suspend fun onUserIdChanged(userId: String?) {
        if (userId == null) {
            setBookId(null)
            setBook(null)
            bookRegistration?.remove()
        } else {
            val bookId = getBookId(userId) ?: return
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

    private suspend fun getBookId(userId: String): String? {
        val snapshot = booksDb.whereArrayContains("users", userId).get().await()
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
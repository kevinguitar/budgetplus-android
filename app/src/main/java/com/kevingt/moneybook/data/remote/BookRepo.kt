package com.kevingt.moneybook.data.remote

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.toObject
import com.kevingt.moneybook.auth.AuthManager
import com.kevingt.moneybook.data.local.PreferenceHolder
import com.kevingt.moneybook.data.local.bindObject
import com.kevingt.moneybook.di.BooksDb
import com.kevingt.moneybook.utils.AppScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

interface BookRepo {

    val bookState: StateFlow<Book?>

}

@Singleton
class BookRepoImpl @Inject constructor(
    authManager: AuthManager,
    preferenceHolder: PreferenceHolder,
    @AppScope appScope: CoroutineScope,
    @BooksDb private val booksDb: CollectionReference
) : BookRepo {

    private var currentBook by preferenceHolder.bindObject<Book>(null)

    private val _bookState = MutableStateFlow(currentBook)
    override val bookState: StateFlow<Book?> get() = _bookState

    private var registration: ListenerRegistration? = null

    init {
        authManager.userState
            .onEach(::observeBook)
            .flowOn(Dispatchers.IO)
            .launchIn(appScope)
    }

    private fun observeBook(user: User?) {
        registration?.remove()
        if (user == null) return

        registration = booksDb
            .whereArrayContains("users", user.id)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Timber.e(e, "Listen failed.")
                    return@addSnapshotListener
                }

                if (snapshot == null || snapshot.isEmpty) {
                    Timber.d("BookRepo: Snapshot is empty")
                    return@addSnapshotListener
                }

                if (snapshot.size() > 1) {
                    Timber.e("This user has more than one book. user=${user.id}")
                }

                val doc = snapshot.first()
                _bookState.value = doc.toObject<Book>().copy(id = doc.id)
            }
    }
}
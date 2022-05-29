package com.kevingt.moneybook.data.remote

import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.kevingt.moneybook.utils.AppScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

interface RecordRepo {

    val recordsState: StateFlow<List<Record>?>

    fun createRecord(record: Record)

    fun deleteRecord(recordId: String)

}

@Singleton
class RecordRepoImpl @Inject constructor(
    private val bookRepo: BookRepo,
    @AppScope appScope: CoroutineScope,
) : RecordRepo {

    private val _recordsState = MutableStateFlow<List<Record>?>(null)
    override val recordsState: StateFlow<List<Record>?> get() = _recordsState

    private val recordsDb
        get() = Firebase.firestore.collection("books")
            .document(requireNotNull(bookRepo.bookIdState.value) { "Book id is null" })
            .collection("records")

    init {
        bookRepo.bookIdState
            .onEach(::observeRecords)
            .launchIn(appScope)
    }

    override fun createRecord(record: Record) {
        recordsDb.add(record)
    }

    override fun deleteRecord(recordId: String) {
        recordsDb.document(recordId).delete()
    }

    private var recordsRegistration: ListenerRegistration? = null

    private fun observeRecords(bookId: String?) {
        recordsRegistration?.remove()
        bookId ?: return

        recordsRegistration = recordsDb
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Timber.e(e, "Listen failed.")
                    return@addSnapshotListener
                }

                if (snapshot == null) {
                    Timber.d("BookRepo: Snapshot is empty")
                    return@addSnapshotListener
                }

                _recordsState.value = snapshot.documents.mapNotNull { doc ->
                    doc.toObject<Record>()?.copy(id = doc.id)
                }
            }
    }
}
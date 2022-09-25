package com.kevlina.budgetplus.book.overview.vm

import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.kevlina.budgetplus.data.remote.Record
import com.kevlina.budgetplus.data.remote.TimePeriod
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 *  Use a singleton class to register the records listener, this will significantly
 *  reduce the db requests for us.
 */
@Singleton
class RecordsObserver @Inject constructor() {

    private val _records = MutableStateFlow<Sequence<Record>>(emptySequence())
    val records: StateFlow<Sequence<Record>> = _records.asStateFlow()

    private var currentRegistrationConfig: Pair<String, TimePeriod>? = null
    private var recordsRegistration: ListenerRegistration? = null

    fun observeRecords(bookId: String, period: TimePeriod) {
        Timber.d("OOO: observe attempt")
        val newConfig = bookId to period
        if (currentRegistrationConfig == newConfig) {
            // Do not establish the listener again if the config is the same
            return
        }

        Timber.d("OOO: observing")
        currentRegistrationConfig = newConfig
        recordsRegistration?.remove()
        recordsRegistration = Firebase.firestore
            .collection("books")
            .document(bookId)
            .collection("records")
            .whereGreaterThanOrEqualTo("date", period.from.toEpochDay())
            .whereLessThanOrEqualTo("date", period.until.toEpochDay())
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Timber.e(e, "Listen failed.")
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    _records.value = snapshot.documents
                        .mapNotNull { doc -> doc.toObject<Record>()?.copy(id = doc.id) }
                        .asSequence()
                }
            }
    }
}
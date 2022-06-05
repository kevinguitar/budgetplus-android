package com.kevingt.moneybook.book.overview.vm

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.kevingt.moneybook.data.remote.BookRepo
import com.kevingt.moneybook.data.remote.Record
import com.kevingt.moneybook.data.remote.RecordRepo
import com.kevingt.moneybook.data.remote.RecordType
import com.kevingt.moneybook.utils.mapState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class OverviewViewModel @Inject constructor(
    private val bookRepo: BookRepo,
    private val recordRepo: RecordRepo
) : ViewModel() {

    private val _type = MutableStateFlow(RecordType.Expense)
    val type: StateFlow<RecordType> = _type.asStateFlow()

    //TODO: Consider saving the option to pref
    private val _timePeriod = MutableStateFlow<TimePeriod>(TimePeriod.Month)
    val timePeriod: StateFlow<TimePeriod> = _timePeriod.asStateFlow()

    val fromDate = timePeriod.mapState { it.from }
    val untilDate = timePeriod.mapState { it.until }

    private val _records = MutableStateFlow<Sequence<Record>?>(null)
    val records: StateFlow<Sequence<Record>?> = _records.asStateFlow()

    val recordGroups = records.mapState { records ->
        records.orEmpty()
            .groupBy { it.category }
            .toList()
            .sortedByDescending { (_, v) -> v.sumOf { it.price } }
            .toMap()
    }

    init {
        observeRecords()
    }

    fun setRecordType(type: RecordType) {
        _type.value = type
        observeRecords()
    }

    fun setTimePeriod(timePeriod: TimePeriod) {
        _timePeriod.value = timePeriod
        observeRecords()
    }

    fun deleteRecord(recordId: String) {
        recordRepo.deleteRecord(recordId)
    }

    private var recordsRegistration: ListenerRegistration? = null

    private fun observeRecords() {
        recordsRegistration?.remove()
        recordsRegistration = Firebase.firestore
            .collection("books")
            .document(requireNotNull(bookRepo.bookIdState.value) { "Book id is null" })
            .collection("records")
            .whereEqualTo("type", type.value)
            .whereGreaterThanOrEqualTo("date", fromDate.value.toEpochDay())
            .whereLessThanOrEqualTo("date", untilDate.value.toEpochDay())
            .orderBy("date", Query.Direction.DESCENDING)
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
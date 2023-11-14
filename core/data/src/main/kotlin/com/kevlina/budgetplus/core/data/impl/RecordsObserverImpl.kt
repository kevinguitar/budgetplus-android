package com.kevlina.budgetplus.core.data.impl

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.toObject
import com.kevlina.budgetplus.core.common.AppScope
import com.kevlina.budgetplus.core.common.combineState
import com.kevlina.budgetplus.core.data.BookRepo
import com.kevlina.budgetplus.core.data.RecordsObserver
import com.kevlina.budgetplus.core.data.local.PreferenceHolder
import com.kevlina.budgetplus.core.data.remote.BooksDb
import com.kevlina.budgetplus.core.data.remote.Record
import com.kevlina.budgetplus.core.data.remote.TimePeriod
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.mapNotNull
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 *  Use a singleton class to register the records listener, this will significantly
 *  reduce the db requests for us.
 */
@Singleton
internal class RecordsObserverImpl @Inject constructor(
    @AppScope appScope: CoroutineScope,
    @BooksDb private val booksDb: dagger.Lazy<CollectionReference>,
    preferenceHolder: PreferenceHolder,
    bookRepo: BookRepo,
) : RecordsObserver {

    private val _records = MutableStateFlow<Sequence<Record>?>(null)
    override val records: StateFlow<Sequence<Record>?> = _records.asStateFlow()

    // Cache the time period by book id
    private var periodCache by preferenceHolder.bindObject<Map<String, TimePeriod>>(emptyMap())
    private val timePeriodMap = MutableStateFlow(periodCache)

    override val timePeriod: StateFlow<TimePeriod> = timePeriodMap.combineState(
        other = bookRepo.bookState,
        scope = appScope
    ) { periodMap, book ->
        book?.id?.let(periodMap::get) ?: TimePeriod.Month
    }

    private var currentRegistrationConfig: Pair<String, TimePeriod>? = null
    private var recordsRegistration: ListenerRegistration? = null

    init {
        combine(
            bookRepo.bookState.mapNotNull { it?.id },
            timePeriod,
            ::observeRecords
        ).launchIn(appScope)
    }

    override fun setTimePeriod(bookId: String, period: TimePeriod) {
        // Check if the custom period matches the preset period
        val normalizedPeriod = when (period) {
            TimePeriod.Today -> TimePeriod.Today
            TimePeriod.Week -> TimePeriod.Week
            TimePeriod.Month -> TimePeriod.Month
            TimePeriod.LastMonth -> TimePeriod.LastMonth
            else -> period
        }

        val newMapping = periodCache.toMutableMap()
            .apply { this[bookId] = normalizedPeriod }

        timePeriodMap.value = newMapping
        periodCache = newMapping
    }

    private fun observeRecords(bookId: String, period: TimePeriod) {
        val newConfig = bookId to period
        if (currentRegistrationConfig == newConfig) {
            // Do not establish the listener again if the config is the same
            return
        }

        currentRegistrationConfig = newConfig
        recordsRegistration?.remove()
        recordsRegistration = booksDb.get()
            .document(bookId)
            .collection("records")
            .orderBy("date", Query.Direction.DESCENDING)
            .whereGreaterThanOrEqualTo("date", period.from.toEpochDay())
            .whereLessThanOrEqualTo("date", period.until.toEpochDay())
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Timber.e(e, "RecordsObserver: Listen failed.")
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
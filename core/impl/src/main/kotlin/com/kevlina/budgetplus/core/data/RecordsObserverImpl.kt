package com.kevlina.budgetplus.core.data

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.toObject
import com.kevlina.budgetplus.core.common.AppCoroutineScope
import com.kevlina.budgetplus.core.common.tickerFlow
import com.kevlina.budgetplus.core.data.local.PreferenceHolder
import com.kevlina.budgetplus.core.data.remote.BooksDb
import com.kevlina.budgetplus.core.data.remote.Record
import com.kevlina.budgetplus.core.data.remote.TimePeriod
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEach
import timber.log.Timber
import java.time.LocalDate
import kotlin.time.Duration.Companion.seconds

/**
 *  Use a singleton class to register the records listener, this will significantly
 *  reduce the db requests for us.
 */
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class RecordsObserverImpl(
    @AppCoroutineScope appScope: CoroutineScope,
    @BooksDb private val booksDb: Lazy<CollectionReference>,
    preferenceHolder: PreferenceHolder,
    bookRepo: BookRepo,
) : RecordsObserver {

    // A ticker to refresh the records when a day has passed.
    private val ticker = tickerFlow(10.seconds)
        .map { LocalDate.now() }
        .distinctUntilChanged()
        .drop(1)
        .onEach {
            val bookId = bookRepo.bookState.value?.id ?: return@onEach

            currentRegistrationConfig = null
            observeRecords(bookId, timePeriod.first())
        }

    final override val records: StateFlow<Sequence<Record>?>
        field = MutableStateFlow<Sequence<Record>?>(null)

    // Cache the time period by book id
    private var periodCache by preferenceHolder.bindObject<Map<String, TimePeriod>>(emptyMap())
    private val timePeriodMap = MutableStateFlow(periodCache)

    override val timePeriod: Flow<TimePeriod> = combine(
        timePeriodMap,
        bookRepo.bookState
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
        )
            .flowOn(Dispatchers.Default)
            .launchIn(appScope)

        ticker
            .flowOn(Dispatchers.Default)
            .launchIn(appScope)
    }

    override fun setTimePeriod(bookId: String, period: TimePeriod) {
        // Check if the custom period matches the preset period
        val normalizedPeriod = when (period) {
            TimePeriod.Today -> TimePeriod.Today
            TimePeriod.Week -> TimePeriod.Week
            TimePeriod.Month -> TimePeriod.Month
            TimePeriod.LastMonth -> TimePeriod.LastMonth
            is TimePeriod.Custom -> period
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
        recordsRegistration = booksDb.value
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
                    records.value = snapshot.documents
                        .mapNotNull { doc -> doc.toObject<Record>()?.copy(id = doc.id) }
                        .asSequence()
                }
            }
    }
}
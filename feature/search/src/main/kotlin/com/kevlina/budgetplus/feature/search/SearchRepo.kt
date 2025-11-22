package com.kevlina.budgetplus.feature.search

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.toObject
import com.kevlina.budgetplus.core.common.SnackbarSender
import com.kevlina.budgetplus.core.common.Tracker
import com.kevlina.budgetplus.core.common.bundle
import com.kevlina.budgetplus.core.data.BookRepo
import com.kevlina.budgetplus.core.data.remote.BooksDb
import com.kevlina.budgetplus.core.data.remote.Record
import com.kevlina.budgetplus.feature.search.ui.SearchCategory
import com.kevlina.budgetplus.feature.search.ui.SearchPeriod
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import timber.log.Timber
import java.time.LocalDate

class SearchRepo @Inject constructor(
    @BooksDb private val booksDb: Lazy<CollectionReference>,
    private val bookRepo: BookRepo,
    private val snackbarSender: SnackbarSender,
    private val tracker: Tracker,
) : ViewModel() {

    val query = TextFieldState()
    val category = MutableStateFlow<SearchCategory>(SearchCategory.None)
    val period = MutableStateFlow<SearchPeriod>(SearchPeriod.PastMonth)

    private val dbQueryActivationFlow = combine(
        snapshotFlow { query.text },
        category,
        ::Pair
    )
        .filter { (queryText, category) ->
            queryText.isNotBlank() || category != SearchCategory.None
        }
        .map { Unit }
        .distinctUntilChanged()

    val dbResult: Flow<DbResult> = combine(
        period,
        dbQueryActivationFlow,
        ::Pair
    )
        .flatMapLatest { (period, _) ->
            val bookId = bookRepo.currentBookId
            if (bookId.isNullOrEmpty()) {
                flowOf(DbResult.Empty)
            } else {
                queryFromDb(bookId, period)
            }
        }
        // Reply the latest result to avoid querying DB again
        .shareIn(viewModelScope, SharingStarted.Lazily, replay = 1)

    private var recordsRegistration: ListenerRegistration? = null

    fun endConnection() {
        recordsRegistration?.remove()
        recordsRegistration = null
    }

    private fun queryFromDb(
        bookId: String,
        period: SearchPeriod,
    ): Flow<DbResult> {
        val flow = MutableSharedFlow<DbResult>(
            onBufferOverflow = BufferOverflow.DROP_OLDEST,
            replay = 1
        )

        Timber.d("Search: Performing DB query with period $period")
        flow.tryEmit(DbResult.Loading)

        recordsRegistration?.remove()
        recordsRegistration = booksDb.value
            .document(bookId)
            .collection("records")
            .orderBy("date", Query.Direction.DESCENDING)
            .whereGreaterThanOrEqualTo("date", period.fromDate().toEpochDay())
            .whereLessThanOrEqualTo("date", period.untilDate().toEpochDay())
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    snackbarSender.sendError(e)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val records = snapshot.documents
                        .mapNotNull { doc -> doc.toObject<Record>()?.copy(id = doc.id) }
                    Timber.d("Search: result size ${records.size}")
                    tracker.logEvent(
                        event = "search_queried_from_db",
                        params = bundle { putInt("db_read_count", records.size) }
                    )
                    flow.tryEmit(DbResult.Success(records))
                }
            }

        return flow
    }

    @Suppress("MagicNumber")
    private fun SearchPeriod.fromDate(): LocalDate =
        when (this) {
            SearchPeriod.PastMonth -> LocalDate.now().minusMonths(1)
            SearchPeriod.PastHalfYear -> LocalDate.now().minusMonths(6)
            SearchPeriod.PastYear -> LocalDate.now().minusYears(1)
            is SearchPeriod.Custom -> from
        }

    private fun SearchPeriod.untilDate(): LocalDate =
        when (this) {
            SearchPeriod.PastMonth, SearchPeriod.PastHalfYear, SearchPeriod.PastYear -> LocalDate.now()
            is SearchPeriod.Custom -> until
        }

    sealed interface DbResult {
        data object Empty : DbResult
        data object Loading : DbResult
        data class Success(val records: List<Record>) : DbResult
    }
}
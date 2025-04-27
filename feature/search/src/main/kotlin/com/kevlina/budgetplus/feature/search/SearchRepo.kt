package com.kevlina.budgetplus.feature.search

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.toObject
import com.kevlina.budgetplus.core.common.SnackbarSender
import com.kevlina.budgetplus.core.data.BookRepo
import com.kevlina.budgetplus.core.data.remote.BooksDb
import com.kevlina.budgetplus.core.data.remote.Record
import com.kevlina.budgetplus.feature.search.ui.SearchPeriod
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import java.time.LocalDate
import javax.inject.Inject

class SearchRepo @Inject constructor(
    @BooksDb private val booksDb: dagger.Lazy<CollectionReference>,
    private val bookRepo: BookRepo,
    private val snackbarSender: SnackbarSender,
) : ViewModel() {
    val query = TextFieldState()
    val period = MutableStateFlow<SearchPeriod>(SearchPeriod.PastMonth)

    val dbResult: Flow<DbResult> = combine(
        // Start querying DB only if query is presented
        snapshotFlow { query.text }.filter { it.isNotBlank() },
        period,
        ::Pair
    )
        .distinctUntilChangedBy { it.second }
        .flatMapLatest { (query, period) ->
            val bookId = bookRepo.currentBookId
            if (query.isBlank() || bookId.isNullOrEmpty()) {
                return@flatMapLatest flowOf(DbResult.Empty)
            }

            flow<DbResult> {
                emit(DbResult.Loading)
                Timber.d("Search: Performing DB query with period $period")
                val records = try {
                    booksDb.get()
                        .document(bookId)
                        .collection("records")
                        .orderBy("date", Query.Direction.DESCENDING)
                        .whereGreaterThanOrEqualTo("date", period.fromDate().toEpochDay())
                        .apply {
                            val untilDate = period.untilDate()
                            if (untilDate != null) {
                                whereLessThanOrEqualTo("date", untilDate.toEpochDay())
                            }
                        }
                        .get()
                        .await()
                        .map { doc -> doc.toObject<Record>().copy(id = doc.id) }
                } catch (e: Exception) {
                    snackbarSender.sendError(e)
                    emptyList()
                }
                Timber.d("Search: result size ${records.size}")

                if (records.isEmpty()) {
                    emit(DbResult.Empty)
                } else {
                    emit(DbResult.Success(records))
                }
            }
        }
        // Reply the latest result to avoid querying DB again
        .shareIn(viewModelScope, SharingStarted.Lazily, replay = 1)

    private fun SearchPeriod.fromDate(): LocalDate =
        when (this) {
            SearchPeriod.PastMonth -> LocalDate.now().minusMonths(1)
            SearchPeriod.PastHalfYear -> LocalDate.now().minusMonths(6)
            SearchPeriod.PastYear -> LocalDate.now().minusYears(1)
            is SearchPeriod.Custom -> from
        }

    /**
     * @return null means until today
     */
    private fun SearchPeriod.untilDate(): LocalDate? =
        when (this) {
            SearchPeriod.PastMonth, SearchPeriod.PastHalfYear, SearchPeriod.PastYear -> null
            is SearchPeriod.Custom -> until
        }

    sealed interface DbResult {
        data object Empty : DbResult
        data object Loading : DbResult
        data class Success(val records: List<Record>) : DbResult
    }
}
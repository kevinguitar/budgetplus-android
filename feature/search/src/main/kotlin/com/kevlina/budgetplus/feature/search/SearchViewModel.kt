package com.kevlina.budgetplus.feature.search

import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kevlina.budgetplus.core.common.nav.HistoryDest
import com.kevlina.budgetplus.core.data.AuthManager
import com.kevlina.budgetplus.core.data.BookRepo
import com.kevlina.budgetplus.core.data.RecordRepo
import com.kevlina.budgetplus.core.data.remote.Author
import com.kevlina.budgetplus.core.data.remote.Record
import com.kevlina.budgetplus.feature.record.card.RecordCardUiState
import com.kevlina.budgetplus.feature.search.ui.SearchCategory
import com.kevlina.budgetplus.feature.search.ui.SearchFilterState
import com.kevlina.budgetplus.feature.search.ui.SearchResult
import com.kevlina.budgetplus.feature.search.ui.SearchState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withContext

@HiltViewModel(assistedFactory = SearchViewModel.Factory::class)
class SearchViewModel @AssistedInject constructor(
    @Assisted params: HistoryDest.Search,
    private val searchRepo: SearchRepo,
    private val authManager: AuthManager,
    private val bookRepo: BookRepo,
    private val recordRepo: RecordRepo,
) : ViewModel() {

    private val type = MutableStateFlow(params.type)
    private val category = MutableStateFlow<SearchCategory>(SearchCategory.None)
    private val author = MutableStateFlow<Author?>(null)

    val searchResult = combine(
        searchRepo.dbResult,
        snapshotFlow { searchRepo.query.text },
        type,
        category,
        author
    ) { dbResult, query, type, category, author ->
        if (query.isBlank()) return@combine SearchResult.Empty
        when (dbResult) {
            SearchRepo.DbResult.Empty -> SearchResult.Empty
            SearchRepo.DbResult.Loading -> SearchResult.Loading
            is SearchRepo.DbResult.Success -> withContext(Dispatchers.Default) {
                val localSearchResult = dbResult.records
                    .filter { record ->
                        var include = true
                        if (!deepContains(record.name, query) && query !in record.category) {
                            include = false
                        }
                        if (record.type != type) {
                            include = false
                        }
                        if (category is SearchCategory.Selected && record.category != category.name) {
                            include = false
                        }
                        if (author != null && record.author?.id != author.id) {
                            include = false
                        }
                        include
                    }

                if (localSearchResult.isEmpty()) {
                    SearchResult.Empty
                } else {
                    SearchResult.Success(
                        records = localSearchResult.mapToStates()
                    )
                }
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), SearchResult.Empty)

    val state = SearchState(
        query = searchRepo.query,
        filter = SearchFilterState(
            type = type,
            category = category,
            period = searchRepo.period,
            author = author
        ),
        result = searchResult
    )

    private fun canEditRecord(record: Record): Boolean {
        val myUserId = authManager.userState.value?.id
        return bookRepo.bookState.value?.ownerId == myUserId || record.author?.id == myUserId
    }

    private fun List<Record>.mapToStates(): List<RecordCardUiState> =
        mapIndexed { i, record ->
            RecordCardUiState(
                item = record,
                formattedPrice = record.price.toString(),
                isLast = i == lastIndex,
                canEdit = canEditRecord(record),
                showCategory = true,
                showAuthor = true,
                onEdit = { },
                onDuplicate = { recordRepo.duplicateRecord(record) },
                onDelete = {}
            )
        }

    @AssistedFactory
    interface Factory {
        fun create(params: HistoryDest.Search): SearchViewModel
    }
}
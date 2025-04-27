package com.kevlina.budgetplus.feature.search.ui

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Stable
import com.kevlina.budgetplus.core.common.EventFlow
import com.kevlina.budgetplus.core.common.MutableEventFlow
import com.kevlina.budgetplus.core.common.RecordType
import com.kevlina.budgetplus.core.data.remote.Author
import com.kevlina.budgetplus.core.data.remote.Record
import com.kevlina.budgetplus.feature.record.card.RecordCardUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDate

@Stable
data class SearchState(
    val query: TextFieldState,
    val filter: SearchFilterState,
    val result: SearchResultState,
) {
    companion object {
        val preview = SearchState(
            query = TextFieldState("query"),
            filter = SearchFilterState.preview,
            result = SearchResultState.preview
        )
    }
}

@Stable
data class SearchFilterState(
    val type: StateFlow<RecordType>,
    val category: StateFlow<SearchCategory>,
    val period: StateFlow<SearchPeriod>,
    val author: StateFlow<Author?>,
) {
    companion object {
        val preview = SearchFilterState(
            type = MutableStateFlow(RecordType.Expense),
            category = MutableStateFlow(SearchCategory.None),
            period = MutableStateFlow(SearchPeriod.PastMonth),
            author = MutableStateFlow(null)
        )
    }
}

@Stable
data class SearchResultState(
    val result: StateFlow<SearchResult>,
    val editRecordEvent: EventFlow<Record>,
    val deleteRecordEvent: EventFlow<Record>,
) {
    companion object {
        val preview = SearchResultState(
            result = MutableStateFlow(
                SearchResult.Success(
                    List(12) { RecordCardUiState.preview }
                )
            ),
            editRecordEvent = MutableEventFlow(),
            deleteRecordEvent = MutableEventFlow()
        )
    }
}

sealed interface SearchCategory {
    object None : SearchCategory
    data class Selected(val name: String) : SearchCategory
}

sealed interface SearchPeriod {
    data object PastMonth : SearchPeriod
    data object PastHalfYear : SearchPeriod
    data object PastYear : SearchPeriod
    data class Custom(val from: LocalDate, val until: LocalDate) : SearchPeriod
}

sealed interface SearchResult {
    data object Empty : SearchResult
    data object Loading : SearchResult
    data class Success(val records: List<RecordCardUiState>) : SearchResult
}
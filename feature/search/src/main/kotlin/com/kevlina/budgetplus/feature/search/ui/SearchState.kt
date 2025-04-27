package com.kevlina.budgetplus.feature.search.ui

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Stable
import com.kevlina.budgetplus.core.common.RecordType
import com.kevlina.budgetplus.core.data.remote.Author
import com.kevlina.budgetplus.feature.record.card.RecordCardUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDate

@Stable
data class SearchState(
    val query: TextFieldState,
    val filter: SearchFilterState,
    val result: StateFlow<List<RecordCardUiState>>
) {
    companion object {
        val preview = SearchState(
            query = TextFieldState("query"),
            filter = SearchFilterState.preview,
            result = MutableStateFlow(
                listOf(
                    RecordCardUiState.preview,
                    RecordCardUiState.preview,
                    RecordCardUiState.preview,
                    RecordCardUiState.preview,
                )
            )
        )
    }
}

@Stable
data class SearchFilterState(
    val type: StateFlow<RecordType>,
    val category: StateFlow<SearchCategory>,
    val period: StateFlow<SearchPeriod>,
    val author: StateFlow<Author?>
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
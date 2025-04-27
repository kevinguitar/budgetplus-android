package com.kevlina.budgetplus.feature.search

import androidx.compose.foundation.text.input.TextFieldState
import androidx.lifecycle.ViewModel
import com.kevlina.budgetplus.core.common.nav.HistoryDest
import com.kevlina.budgetplus.core.data.remote.Author
import com.kevlina.budgetplus.feature.search.ui.SearchCategory
import com.kevlina.budgetplus.feature.search.ui.SearchFilterState
import com.kevlina.budgetplus.feature.search.ui.SearchPeriod
import com.kevlina.budgetplus.feature.search.ui.SearchState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow

@HiltViewModel(assistedFactory = SearchViewModel.Factory::class)
class SearchViewModel @AssistedInject constructor(
    @Assisted params: HistoryDest.Search,
) : ViewModel() {

    private val query = TextFieldState()

    private val type = MutableStateFlow(params.type)
    private val category = MutableStateFlow<SearchCategory>(SearchCategory.None)
    private val period = MutableStateFlow(SearchPeriod.PastMonth)
    private val author = MutableStateFlow<Author?>(null)

    val state = SearchState(
        query = query,
        filter = SearchFilterState(
            type = type,
            category = category,
            period = period,
            author = author
        ),
        result = MutableStateFlow(emptyList())
    )

    @AssistedFactory
    interface Factory {
        fun create(params: HistoryDest.Search): SearchViewModel
    }
}
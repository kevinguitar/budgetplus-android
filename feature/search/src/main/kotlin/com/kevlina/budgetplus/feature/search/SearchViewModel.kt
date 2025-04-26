package com.kevlina.budgetplus.feature.search

import androidx.lifecycle.ViewModel
import com.kevlina.budgetplus.core.common.nav.HistoryDest
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel

@HiltViewModel(assistedFactory = SearchViewModel.Factory::class)
class SearchViewModel @AssistedInject constructor(
    @Assisted params: HistoryDest.Search,
) : ViewModel() {


    @AssistedFactory
    interface Factory {
        fun create(params: HistoryDest.Search): SearchViewModel
    }
}
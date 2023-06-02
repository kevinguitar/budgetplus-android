package com.kevlina.budgetplus.feature.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kevlina.budgetplus.core.common.Toaster
import com.kevlina.budgetplus.core.data.AuthManager
import com.kevlina.budgetplus.core.data.RecordRepo
import com.kevlina.budgetplus.core.data.remote.Record
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class SearchViewModel @Inject constructor(
    authManager: AuthManager,
    private val recordRepo: RecordRepo,
    private val toaster: Toaster,
) : ViewModel() {

    val isPremium = authManager.isPremium

    private val _records = MutableStateFlow<List<Record>?>(null)
    val records: StateFlow<List<Record>?> = _records.asStateFlow()

    fun search(query: String) {
        viewModelScope.launch {
            try {
                val records = recordRepo.searchRecords(query)
                _records.value = records
            } catch (e: Exception) {
                toaster.showError(e)
            }
        }
    }
}
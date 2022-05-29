package com.kevingt.moneybook.book.overview

import androidx.lifecycle.ViewModel
import com.kevingt.moneybook.data.remote.RecordRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class OverviewViewModel @Inject constructor(
    private val recordRepo: RecordRepo,
) : ViewModel() {

    val records = recordRepo.recordsState

    fun deleteRecord(id: String) {
        recordRepo.deleteRecord(recordId = id)
    }
}
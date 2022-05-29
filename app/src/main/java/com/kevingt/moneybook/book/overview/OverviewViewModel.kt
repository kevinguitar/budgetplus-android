package com.kevingt.moneybook.book.overview

import androidx.lifecycle.ViewModel
import com.kevingt.moneybook.data.remote.RecordRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class OverviewViewModel @Inject constructor(
    recordRepo: RecordRepo,
) : ViewModel() {

    val records = recordRepo.recordsState
}
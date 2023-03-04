package com.kevlina.budgetplus.book

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kevlina.budgetplus.core.common.RecordType
import com.kevlina.budgetplus.feature.records.RecordsViewModel
import dagger.hilt.android.EntryPointAccessors

@Composable
fun recordsVm(type: RecordType, category: String, authorId: String?): RecordsViewModel {
    val factory = EntryPointAccessors
        .fromActivity<BookActivity.VmFactoryProvider>(LocalContext.current as Activity)
        .detailsVmFactory()

    return viewModel(factory = RecordsViewModel.provideFactory(
        assistedFactory = factory,
        type = type,
        category = category,
        authorId = authorId
    ))
}
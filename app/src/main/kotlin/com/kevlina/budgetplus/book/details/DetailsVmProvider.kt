package com.kevlina.budgetplus.book.details

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kevlina.budgetplus.book.BookActivity
import com.kevlina.budgetplus.book.details.vm.DetailsViewModel
import com.kevlina.budgetplus.core.common.RecordType
import dagger.hilt.android.EntryPointAccessors

@Composable
fun detailsVm(type: RecordType, category: String, authorId: String?): DetailsViewModel {
    val factory = EntryPointAccessors
        .fromActivity<BookActivity.VmFactoryProvider>(LocalContext.current as Activity)
        .detailsVmFactory()

    return viewModel(factory = DetailsViewModel.provideFactory(
        assistedFactory = factory,
        type = type,
        category = category,
        authorId = authorId
    ))
}
package com.kevlina.budgetplus.feature.category.pills

import androidx.lifecycle.ViewModel
import com.kevlina.budgetplus.core.common.mapState
import com.kevlina.budgetplus.core.data.BookRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import javax.inject.Inject

@HiltViewModel
class CategoriesViewModel @Inject constructor(
    bookRepo: BookRepo,
) : ViewModel() {

    val expenseCategories = bookRepo.bookState
        .mapState { it?.expenseCategories.orEmpty().toImmutableList() }

    val incomeCategories = bookRepo.bookState
        .mapState { it?.incomeCategories.orEmpty().toImmutableList() }

}
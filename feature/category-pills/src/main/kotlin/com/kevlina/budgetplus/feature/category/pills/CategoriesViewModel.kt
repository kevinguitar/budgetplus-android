package com.kevlina.budgetplus.feature.category.pills

import com.kevlina.budgetplus.core.common.mapState
import com.kevlina.budgetplus.core.data.BookRepo
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class CategoriesViewModel @Inject constructor(
    bookRepo: BookRepo,
) {

    private val _category = MutableStateFlow<String?>(null)
    val category: StateFlow<String?> = _category.asStateFlow()

    val expenseCategories = bookRepo.bookState
        .mapState { it?.expenseCategories.orEmpty().toImmutableList() }

    val incomeCategories = bookRepo.bookState
        .mapState { it?.incomeCategories.orEmpty().toImmutableList() }

    fun setCategory(category: String?) {
        _category.value = category
    }
}
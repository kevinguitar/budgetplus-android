package com.kevlina.budgetplus.feature.category.pills

import com.kevlina.budgetplus.core.common.mapState
import com.kevlina.budgetplus.core.data.BookRepo
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Inject
class CategoriesViewModel(
    bookRepo: BookRepo,
) {

    val category: StateFlow<String?>
        field = MutableStateFlow<String?>(null)

    val expenseCategories = bookRepo.bookState
        .mapState { it?.expenseCategories.orEmpty() }

    val incomeCategories = bookRepo.bookState
        .mapState { it?.incomeCategories.orEmpty() }

    fun setCategory(newCategory: String?) {
        category.value = newCategory
    }
}
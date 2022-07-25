package com.kevingt.budgetplus.ui

import androidx.lifecycle.ViewModel
import com.kevingt.budgetplus.data.remote.BookRepo
import com.kevingt.budgetplus.utils.mapState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CategoriesViewModel @Inject constructor(
    bookRepo: BookRepo,
) : ViewModel() {

    val expenseCategories = bookRepo.bookState
        .mapState { it?.expenseCategories.orEmpty() }

    val incomeCategories = bookRepo.bookState
        .mapState { it?.incomeCategories.orEmpty() }

}
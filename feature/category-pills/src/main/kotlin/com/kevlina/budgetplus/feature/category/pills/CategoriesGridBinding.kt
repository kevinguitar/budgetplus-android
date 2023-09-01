package com.kevlina.budgetplus.feature.category.pills

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kevlina.budgetplus.core.common.RecordType

@Composable
fun CategoriesGridBinding(
    type: RecordType,
    modifier: Modifier = Modifier,
    onCategorySelected: (String) -> Unit,
    onEditClicked: (() -> Unit)? = null,
    selectedCategory: String? = null,
) {

    val viewModel = hiltViewModel<CategoriesViewModel>()

    val expenseCategories by viewModel.expenseCategories.collectAsStateWithLifecycle()
    val incomeCategories by viewModel.incomeCategories.collectAsStateWithLifecycle()

    CategoriesGrid(
        type = type,
        expenseCategories = expenseCategories,
        incomeCategories = incomeCategories,
        onCategorySelected = onCategorySelected,
        modifier = modifier,
        onEditClicked = onEditClicked,
        selectedCategory = selectedCategory
    )
}
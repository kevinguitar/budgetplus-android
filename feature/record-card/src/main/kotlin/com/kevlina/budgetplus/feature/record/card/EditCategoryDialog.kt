package com.kevlina.budgetplus.feature.record.card

import androidx.compose.runtime.Composable
import com.kevlina.budgetplus.core.common.RecordType
import com.kevlina.budgetplus.core.ui.AppDialog
import com.kevlina.budgetplus.feature.category.pills.CategoriesGridBinding

@Composable
internal fun EditCategoryDialog(
    type: RecordType,
    category: String,
    onCategorySelected: (String) -> Unit,
    onDismiss: () -> Unit,
) {

    AppDialog(
        onDismissRequest = onDismiss
    ) {

        CategoriesGridBinding(
            type = type,
            onCategorySelected = {
                onCategorySelected(it)
                onDismiss()
            },
            selectedCategory = category
        )
    }
}
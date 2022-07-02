package com.kevingt.moneybook.book.category

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.kevingt.moneybook.R
import com.kevingt.moneybook.data.remote.RecordType
import com.kevingt.moneybook.ui.CategoriesActionBtn
import com.kevingt.moneybook.ui.CategoriesGrid
import com.kevingt.moneybook.ui.TopBar

@Composable
fun EditCategoryScreen(
    navController: NavController,
    type: RecordType
) {

    val viewModel = hiltViewModel<EditCategoryViewModel>()

    var editDialogMode by remember { mutableStateOf<CategoryEditMode?>(null) }

    Column {

        TopBar(
            title = stringResource(id = R.string.category_edit_title),
            navigateBack = { navController.navigateUp() }
        )

        CategoriesGrid(
            type = type,
            onCategorySelected = { editDialogMode = CategoryEditMode.Rename(it) },
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp),
            actionBtn = CategoriesActionBtn.Add {
                editDialogMode = CategoryEditMode.Add
            }
        )
    }

    val dialogMode = editDialogMode
    if (dialogMode != null) {

        EditCategoryDialog(
            mode = dialogMode,
            onConfirm = { name -> viewModel.onCategoryEdited(dialogMode, type, name) },
            onDismiss = { editDialogMode = null },
            onDelete = { viewModel.deleteCategory(dialogMode, type) }
        )
    }
}
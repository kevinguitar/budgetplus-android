package com.kevingt.moneybook.book.category

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.accompanist.flowlayout.FlowRow
import com.google.accompanist.flowlayout.SizeMode
import com.kevingt.moneybook.book.record.CategoryCard
import com.kevingt.moneybook.data.remote.RecordType
import com.kevingt.moneybook.ui.TopBar

@Composable
fun EditCategoryScreen(
    navController: NavController,
    type: RecordType
) {

    val viewModel = hiltViewModel<EditCategoryViewModel>()

    var editDialogMode by rememberSaveable { mutableStateOf<CategoryEditMode?>(null) }

    val expenseCategories by viewModel.expenseCategories.collectAsState(initial = emptyList())
    val incomeCategories by viewModel.incomeCategories.collectAsState(initial = emptyList())

    Column {

        TopBar(
            title = "Edit Category",
            navigateBack = { navController.popBackStack() }
        )

        FlowRow(
            mainAxisSize = SizeMode.Expand,
            mainAxisSpacing = 8.dp,
            modifier = Modifier.padding(16.dp)
        ) {

            when (type) {
                RecordType.Expense -> expenseCategories
                RecordType.Income -> incomeCategories
            }.forEach { item ->
                CategoryCard(category = item, isSelected = false) {
                    editDialogMode = CategoryEditMode.Rename(item)
                }
            }

            AddCategoryCard(onClick = { editDialogMode = CategoryEditMode.Add })
        }
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

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun AddCategoryCard(onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        backgroundColor = Color.White,
        onClick = onClick
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        ) {

            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = "Add Category",
                modifier = Modifier.size(16.dp)
            )

            Text(
                text = "Add",
                textAlign = TextAlign.Center,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
            )
        }
    }
}
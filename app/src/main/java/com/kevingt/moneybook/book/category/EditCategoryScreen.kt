package com.kevingt.moneybook.book.category

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.kevingt.moneybook.R
import com.kevingt.moneybook.data.remote.RecordType
import com.kevingt.moneybook.ui.LocalAppColors
import com.kevingt.moneybook.ui.TopBar
import com.kevingt.moneybook.utils.DraggableItem
import com.kevingt.moneybook.utils.dragContainer
import com.kevingt.moneybook.utils.rememberDragDropState

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun EditCategoryScreen(
    navController: NavController,
    type: RecordType
) {

    val viewModel = hiltViewModel<EditCategoryViewModel>()

    val haptic = LocalHapticFeedback.current

    var editDialogMode by remember { mutableStateOf<CategoryEditMode?>(null) }
    var list by remember {
        mutableStateOf(
            when (type) {
                RecordType.Expense -> viewModel.expenseCategories
                RecordType.Income -> viewModel.incomeCategories
            }
        )
    }

    fun updateList(newList: List<String>) {
        list = newList
        viewModel.updateCategories(type, newList)
    }

    val listState = rememberLazyListState()
    val dragDropState = rememberDragDropState(listState) { fromIndex, toIndex ->
        list.toMutableList()
            .apply { add(toIndex, removeAt(fromIndex)) }
            .also(::updateList)
    }

    Column {

        TopBar(
            title = stringResource(id = R.string.category_edit_title),
            navigateBack = { navController.navigateUp() }
        )

        Box(modifier = Modifier.fillMaxSize()) {

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .dragContainer(dragDropState),
                state = listState,
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                itemsIndexed(list, key = { _, item -> item }) { index, item ->

                    DraggableItem(
                        dragDropState = dragDropState,
                        index = index,
                        modifier = Modifier.clickable {
                            // https://issuetracker.google.com/issues/217739504
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        }
                    ) { isDragging ->
                        CategoryCell(category = item, isDragging = isDragging) {
                            editDialogMode = CategoryEditMode.Rename(item)
                        }
                    }
                }
            }

            Surface(
                shape = CircleShape,
                color = LocalAppColors.current.dark,
                onClick = { editDialogMode = CategoryEditMode.Add },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(24.dp)
                    .size(56.dp)
            ) {

                Icon(
                    imageVector = Icons.Rounded.Add,
                    contentDescription = stringResource(id = R.string.cta_add),
                    tint = LocalAppColors.current.light,
                    modifier = Modifier.padding(all = 8.dp)
                )
            }

        }
    }

    val dialogMode = editDialogMode
    if (dialogMode != null) {

        EditCategoryDialog(
            mode = dialogMode,
            onConfirm = { name ->
                val newList = list.toMutableList().apply {
                    when (dialogMode) {
                        CategoryEditMode.Add -> add(name)
                        is CategoryEditMode.Rename -> {
                            val index = indexOf(dialogMode.currentName)
                            if (index != -1) this[index] = name
                        }
                    }
                }
                updateList(newList)
            },
            onDismiss = { editDialogMode = null },
            onDelete = {
                val name = (dialogMode as CategoryEditMode.Rename).currentName
                val newList = list.toMutableList().apply { remove(name) }
                updateList(newList)
            }
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun CategoryCell(
    category: String,
    isDragging: Boolean,
    onClick: () -> Unit
) {

    val elevation by animateDpAsState(if (isDragging) 4.dp else 0.dp)

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = LocalAppColors.current.primaryLight,
        elevation = elevation,
        onClick = onClick
    ) {

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {

            Icon(
                painter = painterResource(id = R.drawable.ic_drag_handle),
                contentDescription = null,
                tint = LocalAppColors.current.dark,
                modifier = Modifier.size(20.dp)
            )

            Text(
                text = category,
                color = LocalAppColors.current.dark
            )
        }
    }
}
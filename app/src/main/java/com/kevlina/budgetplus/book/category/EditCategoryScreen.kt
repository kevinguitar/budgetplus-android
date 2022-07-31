package com.kevlina.budgetplus.book.category

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
import androidx.compose.material.IconButton
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Check
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.kevlina.budgetplus.R
import com.kevlina.budgetplus.book.bubble.vm.BubbleDest
import com.kevlina.budgetplus.data.remote.RecordType
import com.kevlina.budgetplus.ui.AppText
import com.kevlina.budgetplus.ui.ConfirmDialog
import com.kevlina.budgetplus.ui.LocalAppColors
import com.kevlina.budgetplus.ui.TopBar
import com.kevlina.budgetplus.utils.DraggableItem
import com.kevlina.budgetplus.utils.dragContainer
import com.kevlina.budgetplus.utils.rememberDragDropState
import com.kevlina.budgetplus.utils.thenIf

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun EditCategoryScreen(
    navController: NavController,
    type: RecordType
) {

    val viewModel = hiltViewModel<EditCategoryViewModel>()

    val haptic = LocalHapticFeedback.current

    val originalCategories = when (type) {
        RecordType.Expense -> viewModel.expenseCategories
        RecordType.Income -> viewModel.incomeCategories
    }

    var editDialogMode by remember { mutableStateOf<CategoryEditMode?>(null) }
    var isExitDialogShown by remember { mutableStateOf(false) }
    var list by remember { mutableStateOf(originalCategories) }

    val isListModified = originalCategories != list
    val listState = rememberLazyListState()
    val dragDropState = rememberDragDropState(listState) { fromIndex, toIndex ->
        list = list.toMutableList()
            .apply { add(toIndex, removeAt(fromIndex)) }
    }

    Column {

        TopBar(
            title = stringResource(id = R.string.category_edit_title),
            navigateBack = {
                if (isListModified) {
                    isExitDialogShown = true
                } else {
                    navController.navigateUp()
                }
            },
            menuActions = {
                IconButton(
                    onClick = {
                        if (isListModified) {
                            viewModel.updateCategories(type, list)
                            navController.navigateUp()
                        }
                    },
                    modifier = Modifier.onGloballyPositioned {
                        viewModel.highlightSaveButton(
                            BubbleDest.SaveCategories(
                                size = it.size,
                                offset = it.positionInRoot()
                            )
                        )
                    }
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Check,
                        contentDescription = stringResource(id = R.string.cta_save),
                        tint = LocalAppColors.current.light,
                        modifier = Modifier.thenIf(!isListModified) {
                            Modifier.alpha(0.5F)
                        }
                    )
                }
            }
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
                list = list.toMutableList().apply {
                    when (dialogMode) {
                        CategoryEditMode.Add -> add(name)
                        is CategoryEditMode.Rename -> {
                            val index = indexOf(dialogMode.currentName)
                            if (index != -1) this[index] = name
                        }
                    }
                }
            },
            onDismiss = { editDialogMode = null },
            onDelete = {
                val name = (dialogMode as CategoryEditMode.Rename).currentName
                list = list.toMutableList().apply { remove(name) }
            }
        )
    }

    if (isExitDialogShown) {

        ConfirmDialog(
            message = stringResource(id = R.string.category_edit_unsave_message),
            onConfirm = { navController.navigateUp() },
            onDismiss = { isExitDialogShown = false }
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

            AppText(
                text = category,
            )
        }
    }
}
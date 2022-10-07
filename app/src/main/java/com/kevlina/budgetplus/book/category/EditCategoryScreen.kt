package com.kevlina.budgetplus.book.category

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Check
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kevlina.budgetplus.R
import com.kevlina.budgetplus.book.bubble.vm.BubbleDest
import com.kevlina.budgetplus.book.bubble.vm.BubbleShape
import com.kevlina.budgetplus.data.remote.RecordType
import com.kevlina.budgetplus.ui.AppText
import com.kevlina.budgetplus.ui.AppTheme
import com.kevlina.budgetplus.ui.ConfirmDialog
import com.kevlina.budgetplus.ui.DraggableItem
import com.kevlina.budgetplus.ui.LocalAppColors
import com.kevlina.budgetplus.ui.MenuAction
import com.kevlina.budgetplus.ui.TopBar
import com.kevlina.budgetplus.ui.dragContainer
import com.kevlina.budgetplus.ui.rememberDragDropState
import com.kevlina.budgetplus.utils.Navigator
import com.kevlina.budgetplus.utils.thenIf

@Composable
fun EditCategoryScreen(
    navigator: Navigator,
    type: RecordType,
) {

    val viewModel = hiltViewModel<EditCategoryViewModel>()

    val haptic = LocalHapticFeedback.current

    val originalCategories = when (type) {
        RecordType.Expense -> viewModel.expenseCategories
        RecordType.Income -> viewModel.incomeCategories
    }

    var editDialogMode by remember { mutableStateOf<CategoryEditMode?>(null) }
    var isExitDialogShown by remember { mutableStateOf(false) }
    var list by rememberSaveable { mutableStateOf(originalCategories) }

    val isListModified = originalCategories != list
    val listState = rememberLazyListState()
    val dragDropState = rememberDragDropState(listState) { fromIndex, toIndex ->
        list = list.toMutableList()
            .apply { add(toIndex, removeAt(fromIndex)) }
    }

    val bubbleShape = with(LocalDensity.current) {
        BubbleShape.RoundedRect(12.dp.toPx())
    }

    Column {

        TopBar(
            title = stringResource(id = R.string.category_edit_title),
            navigateBack = {
                if (isListModified) {
                    isExitDialogShown = true
                } else {
                    navigator.navigateUp()
                }
            },
            menuActions = {
                MenuAction(
                    imageVector = Icons.Rounded.Check,
                    description = stringResource(id = R.string.cta_save),
                    enabled = isListModified,
                    modifier = Modifier.onGloballyPositioned {
                        viewModel.highlightSaveButton(
                            BubbleDest.SaveCategories(
                                size = it.size,
                                offset = it.positionInRoot()
                            )
                        )
                    },
                    onClick = {
                        viewModel.updateCategories(type, list)
                        navigator.navigateUp()
                    }
                )
            }
        )

        Box(
            modifier = Modifier
                .weight(1F)
                .fillMaxSize()
        ) {

            LazyColumn(
                modifier = Modifier
                    .width(AppTheme.maxContentWidth)
                    .fillMaxHeight()
                    .align(Alignment.Center)
                    .dragContainer(dragDropState),
                state = listState,
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                itemsIndexed(
                    items = list,
                    key = { _, item -> item },
                    contentType = { _, _ -> "category" }
                ) { index, item ->

                    DraggableItem(
                        dragDropState = dragDropState,
                        index = index,
                        modifier = Modifier.clickable {
                            // https://issuetracker.google.com/issues/217739504
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        }
                    ) { isDragging ->

                        CategoryCell(
                            category = item,
                            isDragging = isDragging,
                            modifier = Modifier.thenIf(index == 0) {
                                Modifier.onGloballyPositioned {
                                    viewModel.highlightCategoryHint(
                                        BubbleDest.EditCategoriesHint(
                                            size = it.size,
                                            offset = it.positionInRoot(),
                                            shape = bubbleShape
                                        )
                                    )
                                }
                            }
                        ) {
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
                        CategoryEditMode.Add -> if (name !in this) add(name)
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
            onConfirm = { navigator.navigateUp() },
            onDismiss = { isExitDialogShown = false }
        )
    }
}

@Composable
private fun CategoryCell(
    category: String,
    isDragging: Boolean,
    modifier: Modifier,
    onClick: () -> Unit,
) {

    val elevation by animateDpAsState(if (isDragging) 4.dp else 0.dp)

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = LocalAppColors.current.light,
        border = BorderStroke(1.dp, LocalAppColors.current.primaryLight),
        elevation = elevation,
        onClick = onClick,
        modifier = modifier
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
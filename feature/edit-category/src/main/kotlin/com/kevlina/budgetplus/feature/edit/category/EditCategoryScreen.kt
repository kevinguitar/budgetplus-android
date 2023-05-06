package com.kevlina.budgetplus.feature.edit.category

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kevlina.budgetplus.core.common.R
import com.kevlina.budgetplus.core.common.RecordType
import com.kevlina.budgetplus.core.common.nav.Navigator
import com.kevlina.budgetplus.core.ui.AppTheme
import com.kevlina.budgetplus.core.ui.ConfirmDialog
import com.kevlina.budgetplus.core.ui.Icon
import com.kevlina.budgetplus.core.ui.LocalAppColors
import com.kevlina.budgetplus.core.ui.MenuAction
import com.kevlina.budgetplus.core.ui.Surface
import com.kevlina.budgetplus.core.ui.TopBar
import com.kevlina.budgetplus.core.ui.bubble.BubbleDest
import com.kevlina.budgetplus.core.ui.bubble.BubbleShape
import com.kevlina.budgetplus.core.ui.thenIf
import org.burnoutcrew.reorderable.ReorderableItem
import org.burnoutcrew.reorderable.detectReorder
import org.burnoutcrew.reorderable.detectReorderAfterLongPress
import org.burnoutcrew.reorderable.rememberReorderableLazyListState
import org.burnoutcrew.reorderable.reorderable

@Composable
fun EditCategoryScreen(
    navigator: Navigator,
    type: RecordType,
) {

    val viewModel = hiltViewModel<EditCategoryViewModel>()

    val originalCategories = when (type) {
        RecordType.Expense -> viewModel.expenseCategories
        RecordType.Income -> viewModel.incomeCategories
    }

    var editDialogMode by remember { mutableStateOf<CategoryEditMode?>(null) }
    var isExitDialogShown by remember { mutableStateOf(false) }
    var list by rememberSaveable { mutableStateOf(originalCategories) }

    val isListModified = originalCategories != list
    val reorderableState = rememberReorderableLazyListState(
        onMove = { (fromIndex, _), (toIndex, _) ->
            list = list.toMutableList()
                .apply { add(toIndex, removeAt(fromIndex)) }
        }
    )

    Column {

        TopBar(
            title = stringResource(id = R.string.category_edit_title),
            navigateUp = {
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
                    enabled = originalCategories != list,
                    modifier = Modifier.onPlaced {
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
                    .reorderable(reorderableState)
                    .detectReorderAfterLongPress(reorderableState),
                state = reorderableState.listState,
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                itemsIndexed(
                    items = list,
                    key = { _, item -> item },
                    contentType = { _, _ -> "category" }
                ) { index, item ->

                    ReorderableItem(
                        reorderableState = reorderableState,
                        key = item,
                        index = index,
                    ) { isDragging ->

                        CategoryCell(
                            category = item,
                            isDragging = isDragging,
                            onClick = { editDialogMode = CategoryEditMode.Rename(item) },
                            handlerModifier = Modifier.detectReorder(reorderableState),
                            modifier = Modifier.thenIf(index == 0) {
                                val bubbleShape = with(LocalDensity.current) {
                                    BubbleShape.RoundedRect(12.dp.toPx())
                                }

                                Modifier.onPlaced {
                                    viewModel.highlightCategoryHint(
                                        BubbleDest.EditCategoriesHint(
                                            size = it.size,
                                            offset = it.positionInRoot(),
                                            shape = bubbleShape
                                        )
                                    )
                                }
                            }
                        )
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
                if (name in list) {
                    viewModel.showCategoryExistError(name)
                    return@EditCategoryDialog
                }

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
            onConfirm = {
                navigator.navigateUp()
                isExitDialogShown = false
            },
            onDismiss = { isExitDialogShown = false }
        )
    }
}
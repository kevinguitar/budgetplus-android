package com.kevlina.budgetplus.feature.edit.category

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.kevlina.budgetplus.core.common.R
import com.kevlina.budgetplus.core.common.RecordType
import com.kevlina.budgetplus.core.ui.ConfirmDialog
import com.kevlina.budgetplus.core.ui.MenuAction
import com.kevlina.budgetplus.core.ui.TopBar
import com.kevlina.budgetplus.core.ui.bubble.BubbleDest
import kotlinx.coroutines.launch
import sh.calvin.reorderable.rememberReorderableLazyListState

@Composable
fun EditCategoryScreen(
    vm: EditCategoryViewModel = hiltViewModel(),
    navController: NavController,
    type: RecordType,
) {

    val originalCategories = when (type) {
        RecordType.Expense -> vm.expenseCategories
        RecordType.Income -> vm.incomeCategories
    }

    var editDialogMode by remember { mutableStateOf<CategoryEditMode?>(null) }
    var isExitDialogShown by remember { mutableStateOf(false) }
    var list by rememberSaveable { mutableStateOf(originalCategories) }

    val coroutineScope = rememberCoroutineScope()
    val lazyListState = rememberLazyListState()
    val reorderableState = rememberReorderableLazyListState(lazyListState) { from, to ->
        list = list.toMutableList()
            .apply { add(to.index, removeAt(from.index)) }
    }

    fun navigateUp() {
        if (originalCategories != list) {
            isExitDialogShown = true
        } else {
            navController.navigateUp()
        }
    }

    if (originalCategories != list) {
        BackHandler(onBack = ::navigateUp)
    }

    Column {
        TopBar(
            title = stringResource(id = R.string.category_edit_title),
            navigateUp = ::navigateUp,
            menuActions = {
                MenuAction(
                    imageVector = Icons.Rounded.Check,
                    description = stringResource(id = R.string.cta_save),
                    enabled = originalCategories != list,
                    modifier = Modifier.onPlaced {
                        vm.highlightSaveButton(
                            BubbleDest.SaveCategories(
                                size = it.size,
                                offset = it.positionInRoot()
                            )
                        )
                    },
                    onClick = {
                        vm.updateCategories(type, list)
                        navController.navigateUp()
                    }
                )
            }
        )

        EditCategoryContent(
            modifier = Modifier.weight(1F),
            categories = list,
            lazyListState = lazyListState,
            reorderableState = reorderableState,
            onDialogEditClick = { editDialogMode = it },
            showEditCategoriesBubble = vm::highlightCategoryHint
        )
    }

    val dialogMode = editDialogMode
    if (dialogMode != null) {
        EditCategoryDialog(
            mode = dialogMode,
            onConfirm = { newName ->
                if (newName in list) {
                    vm.showCategoryExistError(newName)
                    return@EditCategoryDialog
                }

                list = list.toMutableList().apply {
                    when (dialogMode) {
                        CategoryEditMode.Add -> add(newName)
                        is CategoryEditMode.Rename -> {
                            vm.onCategoryRenamed(dialogMode.currentName, newName)
                            val index = indexOf(dialogMode.currentName)
                            if (index != -1) {
                                this[index] = newName
                            }
                        }
                    }
                }

                // If a new category is added, scroll to the bottom of the list.
                if (dialogMode is CategoryEditMode.Add) {
                    coroutineScope.launch {
                        lazyListState.animateScrollToItem(list.lastIndex)
                    }
                }
            },
            onDismiss = { editDialogMode = null },
            onDelete = {
                val name = (dialogMode as CategoryEditMode.Rename).currentName
                vm.onCategoryDeleted(name)
                list = list.toMutableList().apply { remove(name) }
            }
        )
    }

    if (isExitDialogShown) {
        ConfirmDialog(
            message = stringResource(id = R.string.unsaved_warning_message),
            onConfirm = {
                navController.navigateUp()
                isExitDialogShown = false
            },
            onDismiss = { isExitDialogShown = false }
        )
    }
}
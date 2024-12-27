package com.kevlina.budgetplus.feature.edit.category

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.kevlina.budgetplus.core.common.R
import com.kevlina.budgetplus.core.common.RecordType
import com.kevlina.budgetplus.core.common.nav.Navigator
import com.kevlina.budgetplus.core.ui.ConfirmDialog
import com.kevlina.budgetplus.core.ui.MenuAction
import com.kevlina.budgetplus.core.ui.TopBar
import com.kevlina.budgetplus.core.ui.bubble.BubbleDest
import org.burnoutcrew.reorderable.rememberReorderableLazyListState

@Composable
fun EditCategoryScreen(
    vm: EditCategoryViewModel = hiltViewModel(),
    navigator: Navigator,
    type: RecordType,
) {

    val originalCategories = when (type) {
        RecordType.Expense -> vm.expenseCategories
        RecordType.Income -> vm.incomeCategories
    }

    var editDialogMode by remember { mutableStateOf<CategoryEditMode?>(null) }
    var isExitDialogShown by remember { mutableStateOf(false) }
    var list by rememberSaveable { mutableStateOf(originalCategories) }

    val reorderableState = rememberReorderableLazyListState(
        onMove = { (fromIndex, _), (toIndex, _) ->
            list = list.toMutableList()
                .apply { add(toIndex, removeAt(fromIndex)) }
        }
    )

    fun navigateUp() {
        if (originalCategories != list) {
            isExitDialogShown = true
        } else {
            navigator.navigateUp()
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
                        navigator.navigateUp()
                    }
                )
            }
        )

        EditCategoryContent(
            modifier = Modifier.weight(1F),
            categories = list,
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
                navigator.navigateUp()
                isExitDialogShown = false
            },
            onDismiss = { isExitDialogShown = false }
        )
    }
}
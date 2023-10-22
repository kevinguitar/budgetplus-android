package com.kevlina.budgetplus.feature.record.card

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kevlina.budgetplus.core.common.R
import com.kevlina.budgetplus.core.common.plainPriceString
import com.kevlina.budgetplus.core.data.remote.Record
import com.kevlina.budgetplus.core.ui.AppDialog
import com.kevlina.budgetplus.core.ui.ConfirmDialog
import com.kevlina.budgetplus.core.ui.DatePickerDialog
import com.kevlina.budgetplus.core.ui.FontSize
import com.kevlina.budgetplus.core.ui.SingleDatePicker
import com.kevlina.budgetplus.core.ui.Text
import com.kevlina.budgetplus.core.ui.TextField
import com.kevlina.budgetplus.core.ui.rippleClick
import com.kevlina.budgetplus.core.ui.wrapped
import com.kevlina.budgetplus.feature.category.pills.CategoriesGrid
import com.kevlina.budgetplus.feature.category.pills.CategoryCard
import com.kevlina.budgetplus.feature.category.pills.toUiState
import kotlinx.coroutines.flow.MutableStateFlow
import java.time.LocalDate

@Composable
fun EditRecordDialog(
    editRecord: Record,
    vm: EditRecordViewModel = hiltViewModel(),
    onDismiss: () -> Unit,
) {

    var date by remember {
        mutableStateOf(LocalDate.ofEpochDay(editRecord.date).wrapped())
    }

    var category by remember {
        mutableStateOf(editRecord.category)
    }

    var name by remember {
        mutableStateOf(TextFieldValue(
            text = editRecord.name,
            selection = TextRange(editRecord.name.length)
        ))
    }

    var priceText by remember {
        val text = editRecord.price.plainPriceString
        mutableStateOf(TextFieldValue(
            text = text,
            selection = TextRange(text.length)
        ))
    }

    var dialogState by remember { mutableStateOf(EditRecordDialogState.ShowRecord) }

    val (nameFocus, priceFocus) = remember { FocusRequester.createRefs() }
    val isSaveEnabled by remember {
        derivedStateOf {
            name.text.isNotBlank() && priceText.text.isNotBlank()
        }
    }

    fun confirmEdit(editBatch: Boolean = false) {
        vm.editRecord(
            record = editRecord,
            newDate = date.value,
            newCategory = category,
            newName = name.text,
            newPriceText = priceText.text,
            editBatch = editBatch
        )
    }

    when (dialogState) {
        EditRecordDialogState.ShowRecord -> {
            AppDialog(onDismissRequest = onDismiss) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    Text(
                        text = stringResource(id = R.string.record_edit_title),
                        fontSize = FontSize.Large,
                        fontWeight = FontWeight.SemiBold
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {

                        CategoryCard(
                            category = category,
                            isSelected = false,
                            onClick = { dialogState = EditRecordDialogState.PickingCategory }
                        )

                        SingleDatePicker(
                            date = date,
                            modifier = Modifier
                                .rippleClick { dialogState = EditRecordDialogState.PickingDate }
                                .padding(vertical = 4.dp)
                        )
                    }

                    TextField(
                        value = name,
                        onValueChange = { name = it },
                        modifier = Modifier.focusRequester(nameFocus),
                        title = stringResource(id = R.string.record_note),
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Sentences,
                            imeAction = ImeAction.Next
                        )
                    )

                    TextField(
                        value = priceText,
                        onValueChange = { priceText = it },
                        modifier = Modifier.focusRequester(priceFocus),
                        title = stringResource(id = R.string.record_price),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        onDone = {
                            if (editRecord.isBatched) {
                                dialogState = EditRecordDialogState.BatchEditing
                            } else {
                                confirmEdit()
                                onDismiss()
                            }
                        }
                    )

                    EditRecordActions(
                        onDeleteClicked = {
                            dialogState = if (editRecord.isBatched) {
                                EditRecordDialogState.BatchDeleting
                            } else {
                                EditRecordDialogState.DeleteConfirmation
                            }
                        },
                        isSaveEnabled = isSaveEnabled,
                        onSaveClicked = {
                            if (editRecord.isBatched) {
                                dialogState = EditRecordDialogState.BatchEditing
                            } else {
                                confirmEdit()
                                onDismiss()
                            }
                        }
                    )
                }
            }
        }

        EditRecordDialogState.PickingDate -> {
            DatePickerDialog(
                date = date,
                onDatePicked = { date = it.wrapped() },
                onDismiss = { dialogState = EditRecordDialogState.ShowRecord }
            )
        }

        EditRecordDialogState.PickingCategory -> {
            AppDialog(
                onDismissRequest = { dialogState = EditRecordDialogState.ShowRecord }
            ) {
                CategoriesGrid(
                    uiState = vm.categoriesVm.toUiState(
                        type = MutableStateFlow(editRecord.type),
                        selectedCategory = MutableStateFlow(category),
                        onCategorySelected = {
                            category = it
                            dialogState = EditRecordDialogState.ShowRecord
                        },
                    )
                )
            }
        }

        EditRecordDialogState.DeleteConfirmation -> {
            ConfirmDialog(
                message = stringResource(id = R.string.record_confirm_delete),
                onConfirm = {
                    vm.deleteRecord(editRecord)
                    onDismiss()
                },
                onDismiss = { dialogState = EditRecordDialogState.ShowRecord }
            )
        }

        EditRecordDialogState.BatchEditing -> {
            EditBatchDialog(
                onDismiss = { dialogState = EditRecordDialogState.ShowRecord },
                text = stringResource(id = R.string.batch_record_edit_confirmation),
                onSelectOne = {
                    confirmEdit()
                    onDismiss()
                },
                onSelectAll = {
                    confirmEdit(editBatch = true)
                    onDismiss()
                }
            )
        }

        EditRecordDialogState.BatchDeleting -> {
            EditBatchDialog(
                onDismiss = { dialogState = EditRecordDialogState.ShowRecord },
                text = stringResource(id = R.string.batch_record_delete_confirmation),
                onSelectOne = {
                    vm.deleteRecord(editRecord)
                    onDismiss()
                },
                onSelectAll = {
                    vm.deleteRecord(editRecord, deleteBatch = true)
                    onDismiss()
                }
            )
        }
    }

    LaunchedEffect(Unit) {
        nameFocus.requestFocus()
    }
}

internal enum class EditRecordDialogState {
    ShowRecord,
    PickingDate,
    PickingCategory,
    DeleteConfirmation,
    BatchEditing,
    BatchDeleting,
}
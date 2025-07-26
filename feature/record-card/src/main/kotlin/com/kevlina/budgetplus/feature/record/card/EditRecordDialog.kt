package com.kevlina.budgetplus.feature.record.card

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.rememberTextFieldState
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
import androidx.compose.ui.focus.FocusRequester.Companion.FocusRequesterFactory.component1
import androidx.compose.ui.focus.FocusRequester.Companion.FocusRequesterFactory.component2
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kevlina.budgetplus.core.common.R
import com.kevlina.budgetplus.core.data.parseToPrice
import com.kevlina.budgetplus.core.data.plainPriceString
import com.kevlina.budgetplus.core.data.remote.Record
import com.kevlina.budgetplus.core.theme.LocalAppColors
import com.kevlina.budgetplus.core.ui.AppDialog
import com.kevlina.budgetplus.core.ui.Button
import com.kevlina.budgetplus.core.ui.DatePickerDialog
import com.kevlina.budgetplus.core.ui.FontSize
import com.kevlina.budgetplus.core.ui.InputDialog
import com.kevlina.budgetplus.core.ui.SingleDatePicker
import com.kevlina.budgetplus.core.ui.Text
import com.kevlina.budgetplus.core.ui.TextField
import com.kevlina.budgetplus.core.ui.rippleClick
import com.kevlina.budgetplus.feature.category.pills.CategoriesGrid
import com.kevlina.budgetplus.feature.category.pills.CategoryCard
import com.kevlina.budgetplus.feature.category.pills.toState
import kotlinx.coroutines.flow.MutableStateFlow
import timber.log.Timber
import java.time.LocalDate

@Composable
fun EditRecordDialog(
    editRecord: Record,
    vm: EditRecordViewModel = hiltViewModel(),
    onDismiss: () -> Unit,
) {
    var date by remember {
        mutableStateOf(LocalDate.ofEpochDay(editRecord.date))
    }

    var category by remember {
        mutableStateOf(editRecord.category)
    }

    val name = rememberTextFieldState(initialText = editRecord.name)

    val priceText = rememberTextFieldState(initialText = editRecord.price.plainPriceString)

    var dialogState by remember { mutableStateOf(EditRecordDialogState.ShowRecord) }

    val (nameFocus, priceFocus) = remember { FocusRequester.createRefs() }
    val isSaveEnabled by remember {
        derivedStateOf {
            name.text.isNotBlank() && priceText.text.isPriceTextValid()
        }
    }

    fun confirmEdit(editBatch: Boolean = false) {
        vm.editRecord(
            record = editRecord,
            newDate = date,
            newCategory = category,
            newName = name.text.toString(),
            newPriceText = priceText.text.toString(),
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
                        state = name,
                        modifier = Modifier.focusRequester(nameFocus),
                        title = stringResource(id = R.string.record_note),
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Sentences,
                            imeAction = ImeAction.Next
                        )
                    )

                    TextField(
                        state = priceText,
                        modifier = Modifier.focusRequester(priceFocus),
                        title = stringResource(id = R.string.record_price),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = if (isSaveEnabled) ImeAction.Done else ImeAction.None
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

                    Button(
                        enabled = isSaveEnabled,
                        onClick = {
                            if (editRecord.isBatched) {
                                dialogState = EditRecordDialogState.BatchEditing
                            } else {
                                confirmEdit()
                                onDismiss()
                            }
                        }
                    ) {
                        Text(
                            text = stringResource(id = R.string.cta_save),
                            color = LocalAppColors.current.light,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        EditRecordDialogState.PickingDate -> {
            DatePickerDialog(
                date = date,
                onDatePicked = { date = it },
                onDismiss = { dialogState = EditRecordDialogState.ShowRecord }
            )
        }

        EditRecordDialogState.PickingCategory -> {
            AppDialog(
                onDismissRequest = { dialogState = EditRecordDialogState.ShowRecord }
            ) {
                CategoriesGrid(
                    state = vm.categoriesVm.toState(
                        type = MutableStateFlow(editRecord.type),
                        selectedCategory = MutableStateFlow(category),
                        onCategorySelected = {
                            category = it
                            dialogState = EditRecordDialogState.ShowRecord
                        },
                        onAddClicked = if (vm.canAddCategory) {
                            { dialogState = EditRecordDialogState.AddingCategory }
                        } else null
                    )
                )
            }
        }

        EditRecordDialogState.AddingCategory -> {
            InputDialog(
                title = stringResource(id = R.string.category_title),
                buttonText = stringResource(id = R.string.cta_add),
                onButtonClicked = { newCategory ->
                    vm.addCategory(editRecord.type, newCategory)
                },
                onDismiss = { dialogState = EditRecordDialogState.PickingCategory }
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
    }

    LaunchedEffect(Unit) {
        nameFocus.requestFocus()
    }
}

private fun CharSequence.isPriceTextValid(): Boolean {
    return isNotBlank() && try {
        parseToPrice() > 0.0
    } catch (e: Exception) {
        Timber.d(e, "Invalid price editing: $this")
        false
    }
}

internal enum class EditRecordDialogState {
    ShowRecord,
    PickingDate,
    PickingCategory,
    AddingCategory,
    BatchEditing,
}
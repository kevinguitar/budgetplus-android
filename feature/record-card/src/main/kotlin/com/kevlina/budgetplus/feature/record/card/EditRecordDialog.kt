package com.kevlina.budgetplus.feature.record.card

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
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
import com.kevlina.budgetplus.core.ui.Button
import com.kevlina.budgetplus.core.ui.ConfirmDialog
import com.kevlina.budgetplus.core.ui.DatePickerDialog
import com.kevlina.budgetplus.core.ui.FontSize
import com.kevlina.budgetplus.core.ui.LocalAppColors
import com.kevlina.budgetplus.core.ui.SingleDatePicker
import com.kevlina.budgetplus.core.ui.Text
import com.kevlina.budgetplus.core.ui.TextField
import com.kevlina.budgetplus.core.ui.rippleClick
import java.time.LocalDate

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun EditRecordDialog(
    editRecord: Record,
    onDismiss: () -> Unit,
) {

    val vm = hiltViewModel<EditRecordViewModel>()

    var date by remember {
        mutableStateOf(LocalDate.ofEpochDay(editRecord.date))
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

    var showDatePickerDialog by remember { mutableStateOf(false) }
    var showDeleteConfirmationDialog by remember { mutableStateOf(false) }
    var showEditBatchDialog by remember { mutableStateOf(false) }
    var showDeleteBatchDialog by remember { mutableStateOf(false) }

    val (nameFocus, priceFocus) = remember { FocusRequester.createRefs() }
    val isSaveEnabled by remember {
        derivedStateOf {
            name.text.isNotBlank() && priceText.text.isNotBlank()
        }
    }

    fun confirmEdit(editBatch: Boolean = false) {
        vm.editRecord(
            record = editRecord,
            newDate = date,
            newName = name.text,
            newPriceText = priceText.text,
            editBatch = editBatch
        )
    }

    // Do not stack the dialog, it causes some weird ui issue
    if (!showDeleteConfirmationDialog && !showEditBatchDialog && !showDeleteBatchDialog) {

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

                SingleDatePicker(
                    date = date,
                    modifier = Modifier.rippleClick { showDatePickerDialog = true }
                )

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
                            showEditBatchDialog = true
                        } else {
                            confirmEdit()
                            onDismiss()
                        }
                    }
                )

                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {

                    Button(onClick = {
                        if (editRecord.isBatched) {
                            showDeleteBatchDialog = true
                        } else {
                            showDeleteConfirmationDialog = true
                        }
                    }) {
                        Text(
                            text = stringResource(id = R.string.cta_delete),
                            color = LocalAppColors.current.light,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Button(
                        enabled = isSaveEnabled,
                        onClick = {
                            if (editRecord.isBatched) {
                                showEditBatchDialog = true
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
    }

    if (showDatePickerDialog) {

        DatePickerDialog(
            date = date,
            onDatePicked = { date = it },
            onDismiss = { showDatePickerDialog = false }
        )
    }

    if (showDeleteConfirmationDialog) {

        ConfirmDialog(
            message = stringResource(id = R.string.record_confirm_delete),
            onConfirm = {
                vm.deleteRecord(editRecord)
                showDeleteConfirmationDialog = false
                onDismiss()
            },
            onDismiss = { showDeleteConfirmationDialog = false }
        )
    }

    if (showEditBatchDialog) {

        EditBatchDialog(
            onDismiss = { showEditBatchDialog = false },
            text = stringResource(id = R.string.batch_record_edit_confirmation),
            onSelectOne = {
                confirmEdit()
                showEditBatchDialog = false
                onDismiss()
            },
            onSelectAll = {
                confirmEdit(editBatch = true)
                showEditBatchDialog = false
                onDismiss()
            }
        )
    }

    if (showDeleteBatchDialog) {

        EditBatchDialog(
            onDismiss = { showDeleteBatchDialog = false },
            text = stringResource(id = R.string.batch_record_delete_confirmation),
            onSelectOne = {
                vm.deleteRecord(editRecord)
                showDeleteBatchDialog = false
                onDismiss()
            },
            onSelectAll = {
                vm.deleteRecord(editRecord, deleteBatch = true)
                showDeleteBatchDialog = false
                onDismiss()
            }
        )
    }

    LaunchedEffect(Unit) {
        nameFocus.requestFocus()
    }
}
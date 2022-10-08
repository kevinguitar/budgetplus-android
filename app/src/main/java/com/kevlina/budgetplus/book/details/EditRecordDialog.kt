package com.kevlina.budgetplus.book.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kevlina.budgetplus.R
import com.kevlina.budgetplus.data.remote.Record
import com.kevlina.budgetplus.ui.AppButton
import com.kevlina.budgetplus.ui.AppDialog
import com.kevlina.budgetplus.ui.AppText
import com.kevlina.budgetplus.ui.AppTextField
import com.kevlina.budgetplus.ui.ConfirmDialog
import com.kevlina.budgetplus.ui.DatePickerDialog
import com.kevlina.budgetplus.ui.FontSize
import com.kevlina.budgetplus.ui.LocalAppColors
import com.kevlina.budgetplus.ui.SingleDatePicker
import com.kevlina.budgetplus.utils.rippleClick

@Composable
fun EditRecordDialog(
    editRecord: Record,
    onDismiss: () -> Unit,
) {

    val vm = hiltViewModel<EditRecordViewModel>()

    LaunchedEffect(key1 = editRecord, key2 = vm) {
        vm.setRecord(editRecord)
    }

    val date by vm.date.collectAsState()
    val name by vm.name.collectAsState()
    val priceText by vm.priceText.collectAsState()

    var showDatePickerDialog by remember { mutableStateOf(false) }
    var showDeleteConfirmationDialog by remember { mutableStateOf(false) }

    AppDialog(onDismissRequest = onDismiss) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            AppText(
                text = stringResource(id = R.string.record_edit_title),
                fontSize = FontSize.Large,
                fontWeight = FontWeight.SemiBold
            )

            SingleDatePicker(
                date = date,
                modifier = Modifier.rippleClick { showDatePickerDialog = true }
            )

            AppTextField(
                value = name,
                onValueChange = vm::setName,
                title = stringResource(id = R.string.record_note)
            )

            AppTextField(
                value = priceText,
                onValueChange = vm::setPrice,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                title = stringResource(id = R.string.record_price)
            )

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {

                AppButton(
                    onClick = { showDeleteConfirmationDialog = true },
                ) {
                    AppText(
                        text = stringResource(id = R.string.cta_delete),
                        color = LocalAppColors.current.light,
                        fontWeight = FontWeight.Medium
                    )
                }

                AppButton(
                    onClick = {
                        vm.editRecord()
                        onDismiss()
                    },
                ) {
                    AppText(
                        text = stringResource(id = R.string.cta_save),
                        color = LocalAppColors.current.light,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }

    if (showDatePickerDialog) {

        DatePickerDialog(
            date = date,
            onDatePicked = vm::setDate,
            onDismiss = { showDatePickerDialog = false }
        )
    }

    if (showDeleteConfirmationDialog) {

        ConfirmDialog(
            message = stringResource(id = R.string.record_confirm_delete),
            onConfirm = {
                vm.deleteRecord()
                onDismiss()
            },
            onDismiss = { showDeleteConfirmationDialog = false }
        )
    }
}
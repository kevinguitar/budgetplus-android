package com.kevingt.moneybook.book.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kevingt.moneybook.R
import com.kevingt.moneybook.data.remote.Record
import com.kevingt.moneybook.ui.*

@Composable
fun EditRecordDialog(
    editRecord: Record,
    onDismiss: () -> Unit
) {

    val viewModel = hiltViewModel<EditRecordViewModel>()
    viewModel.setRecord(editRecord)

    val name by viewModel.name.collectAsState()
    val category by viewModel.category.collectAsState()
    val priceText by viewModel.priceText.collectAsState()

    var showDeleteConfirmationDialog by remember { mutableStateOf(false) }

    AppDialog(onDismissRequest = onDismiss) {

        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            CategoriesGrid(
                type = editRecord.type,
                onCategorySelected = viewModel::setCategory,
                selectedCategory = category,
            )

            AppTextField(
                value = name,
                onValueChange = viewModel::setName
            )

            AppTextField(
                value = priceText,
                onValueChange = viewModel::setPrice,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
                modifier = Modifier.fillMaxWidth()
            ) {

                AppButton(
                    onClick = { showDeleteConfirmationDialog = true },
                ) {
                    Text(text = stringResource(id = R.string.cta_delete))
                }

                AppButton(
                    onClick = {
                        viewModel.editRecord()
                        onDismiss()
                    },
                ) {
                    Text(text = stringResource(id = R.string.cta_save))
                }
            }
        }
    }

    if (showDeleteConfirmationDialog) {

        ConfirmDialog(
            message = stringResource(id = R.string.record_confirm_delete),
            onConfirm = {
                viewModel.deleteRecord()
                onDismiss()
            },
            onDismiss = { showDeleteConfirmationDialog = false }
        )
    }
}
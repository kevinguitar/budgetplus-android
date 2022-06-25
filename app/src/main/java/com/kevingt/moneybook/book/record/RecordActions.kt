package com.kevingt.moneybook.book.record

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.kevingt.moneybook.R
import com.kevingt.moneybook.book.record.vm.RecordViewModel
import com.kevingt.moneybook.ui.ConfirmDialog

@Composable
fun RecordActions(
    navController: NavController,
) {

    val viewModel = hiltViewModel<RecordViewModel>()
    val isEditing by viewModel.isEditing.collectAsState()

    val focusManager = LocalFocusManager.current

    var showDeleteConfirmationDialog by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth()) {

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.align(Alignment.Center)
        ) {

            Button(
                onClick = {
                    viewModel.record()
                    focusManager.clearFocus()
                    if (isEditing) {
                        navController.popBackStack()
                    }
                },
            ) {
                Text(text = stringResource(id = if (isEditing) R.string.cta_save else R.string.cta_add))
            }

            if (isEditing) {
                Button(
                    onClick = { showDeleteConfirmationDialog = true },
                ) {
                    Text(text = stringResource(id = R.string.cta_delete))
                }
            }
        }
    }

    if (showDeleteConfirmationDialog) {

        ConfirmDialog(
            message = stringResource(id = R.string.record_confirm_delete),
            onConfirm = {
                viewModel.deleteRecord()
                focusManager.clearFocus()
                navController.popBackStack()
            },
            onDismiss = { showDeleteConfirmationDialog = false }
        )
    }
}
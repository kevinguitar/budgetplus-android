package com.kevlina.budgetplus.feature.record.card

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.kevlina.budgetplus.core.common.R
import com.kevlina.budgetplus.core.data.remote.Record
import com.kevlina.budgetplus.core.ui.ConfirmDialog

@Composable
fun DeleteRecordDialog(
    editRecord: Record,
    onDismiss: () -> Unit,
    vm: EditRecordViewModel = hiltViewModel(),
) {
    if (editRecord.isBatched) {
        EditBatchDialog(
            onDismiss = onDismiss,
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
    } else {
        ConfirmDialog(
            message = stringResource(id = R.string.record_confirm_delete),
            onConfirm = {
                vm.deleteRecord(editRecord)
                onDismiss()
            },
            onDismiss = onDismiss
        )
    }
}
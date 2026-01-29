package com.kevlina.budgetplus.feature.record.card

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.kevlina.budgetplus.core.common.R
import com.kevlina.budgetplus.core.data.remote.Record
import com.kevlina.budgetplus.core.data.remote.isBatched
import com.kevlina.budgetplus.core.ui.ConfirmDialog
import com.kevlina.budgetplus.core.utils.metroViewModel

@Composable
fun DeleteRecordDialog(
    editRecord: Record,
    onDismiss: () -> Unit,
    vm: EditRecordViewModel = metroViewModel(),
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
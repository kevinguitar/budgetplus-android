package com.kevlina.budgetplus.feature.records

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.EventNote
import androidx.compose.material.icons.rounded.Paid
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kevlina.budgetplus.core.common.R
import com.kevlina.budgetplus.core.common.dollar
import com.kevlina.budgetplus.core.common.nav.Navigator
import com.kevlina.budgetplus.core.data.remote.Record
import com.kevlina.budgetplus.core.ui.AppTheme
import com.kevlina.budgetplus.core.ui.MenuAction
import com.kevlina.budgetplus.core.ui.TopBar
import com.kevlina.budgetplus.core.ui.bubble.BubbleDest
import com.kevlina.budgetplus.feature.record.card.DeleteRecordDialog
import com.kevlina.budgetplus.feature.record.card.EditRecordDialog
import com.kevlina.budgetplus.feature.record.card.RecordCard
import com.kevlina.budgetplus.feature.record.card.RecordCardUiState

@Composable
fun RecordsScreen(
    navigator: Navigator,
    vm: RecordsViewModel,
) {

    var editRecordDialog by remember { mutableStateOf<Record?>(null) }
    var deleteRecordDialog by remember { mutableStateOf<Record?>(null) }

    val records by vm.records.collectAsStateWithLifecycle()
    val sortMode by vm.sortMode.collectAsStateWithLifecycle()

    val totalPrice = remember(records) {
        records.orEmpty().sumOf { it.price }.dollar
    }

    // Observe the records, when the last record get deleted, navigate back to the overview screen.
    LaunchedEffect(key1 = records) {
        if (records?.isEmpty() == true) {
            navigator.navigateUp()
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {

        TopBar(
            title = stringResource(id = R.string.overview_details_title, vm.category, totalPrice),
            navigateUp = navigator::navigateUp,
            menuActions = {
                val modifier = Modifier.onPlaced {
                    vm.highlightSortingButton(
                        BubbleDest.RecordsSorting(
                            size = it.size,
                            offset = it.positionInRoot()
                        )
                    )
                }

                when (sortMode) {
                    RecordsSortMode.Date -> MenuAction(
                        imageVector = Icons.Rounded.EventNote,
                        description = stringResource(id = R.string.overview_sort_by_price),
                        onClick = { vm.setSortMode(RecordsSortMode.Price) },
                        modifier = modifier
                    )

                    RecordsSortMode.Price -> MenuAction(
                        imageVector = Icons.Rounded.Paid,
                        description = stringResource(id = R.string.overview_sort_by_date),
                        onClick = { vm.setSortMode(RecordsSortMode.Date) },
                        modifier = modifier
                    )
                }
            }
        )

        LazyColumn(
            modifier = Modifier
                .width(AppTheme.maxContentWidth)
                .align(Alignment.CenterHorizontally)
                .weight(1F)
        ) {

            itemsIndexed(records.orEmpty()) { index, item ->
                RecordCard(uiState = RecordCardUiState(
                    item = item,
                    isLast = index == records?.lastIndex,
                    canEdit = vm.canEditRecord(item),
                    showCategory = false,
                    showAuthor = true,
                    onEdit = { editRecordDialog = item },
                    onDuplicate = { vm.duplicateRecord(item) },
                    onDelete = { deleteRecordDialog = item }
                ))
            }
        }
    }

    editRecordDialog?.let { editRecord ->
        EditRecordDialog(
            editRecord = editRecord,
            onDismiss = { editRecordDialog = null }
        )
    }

    deleteRecordDialog?.let { deleteRecord ->
        DeleteRecordDialog(
            editRecord = deleteRecord,
            onDismiss = { deleteRecordDialog = null }
        )
    }
}
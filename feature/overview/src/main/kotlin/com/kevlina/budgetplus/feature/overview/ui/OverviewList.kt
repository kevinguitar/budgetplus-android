package com.kevlina.budgetplus.feature.overview.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kevlina.budgetplus.core.common.nav.ARG_AUTHOR_ID
import com.kevlina.budgetplus.core.common.nav.HistoryDest
import com.kevlina.budgetplus.core.common.nav.Navigator
import com.kevlina.budgetplus.core.common.nav.navKey
import com.kevlina.budgetplus.core.data.remote.Record
import com.kevlina.budgetplus.core.ui.InfiniteCircularProgress
import com.kevlina.budgetplus.core.ui.thenIf
import com.kevlina.budgetplus.feature.overview.OverviewMode
import com.kevlina.budgetplus.feature.overview.OverviewViewModel
import com.kevlina.budgetplus.feature.record.card.EditRecordDialog
import com.kevlina.budgetplus.feature.record.card.RecordCard
import kotlinx.collections.immutable.persistentListOf

@Composable
fun OverviewList(
    navigator: Navigator,
    modifier: Modifier = Modifier,
    header: (@Composable () -> Unit)? = null,
) {

    val vm = hiltViewModel<OverviewViewModel>()

    val mode by vm.mode.collectAsStateWithLifecycle()
    val type by vm.type.collectAsStateWithLifecycle()
    val selectedAuthor by vm.selectedAuthor.collectAsStateWithLifecycle()
    val totalPrice by vm.totalPrice.collectAsStateWithLifecycle()
    val recordList by vm.recordList.collectAsStateWithLifecycle()
    val recordGroups by vm.recordGroups.collectAsStateWithLifecycle()
    val isSoloAuthor by vm.isSoloAuthor.collectAsStateWithLifecycle()

    var editRecordDialog by remember { mutableStateOf<Record?>(null) }

    LazyColumn(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {

        if (header != null) {

            item(
                key = OverviewUiType.Header.name,
                contentType = OverviewUiType.Header,
                content = { header() }
            )
        }

        val records = recordList
        when {
            records == null -> item(
                key = OverviewUiType.Loader.name,
                contentType = OverviewUiType.Loader,
                content = { InfiniteCircularProgress(modifier = Modifier.padding(top = 32.dp)) }
            )

            records.isEmpty() -> item(
                key = OverviewUiType.ZeroCase.name,
                contentType = OverviewUiType.ZeroCase,
                content = { OverviewZeroCase() }
            )

            mode == OverviewMode.AllRecords -> itemsIndexed(
                items = records,
                key = { _, record -> record.id },
                contentType = { _, _ -> OverviewUiType.Record }
            ) { index, record ->

                RecordCard(
                    modifier = Modifier.thenIf(index == 0) {
                        Modifier.padding(top = 8.dp)
                    },
                    item = record,
                    isLast = index == records.lastIndex,
                    canEdit = vm.canEditRecord(record),
                    showCategory = true,
                    showAuthor = !isSoloAuthor
                ) {
                    editRecordDialog = record
                }
            }

            mode == OverviewMode.GroupByCategories -> itemsIndexed(
                items = recordGroups?.keys?.toList().orEmpty(),
                key = { _, key -> key },
                contentType = { _, _ -> OverviewUiType.Group }
            ) { index, key ->

                OverviewGroup(
                    modifier = Modifier.thenIf(index == 0) {
                        Modifier.padding(top = 8.dp)
                    },
                    category = key,
                    records = recordGroups.orEmpty()[key] ?: persistentListOf(),
                    totalPrice = totalPrice,
                    color = overviewColors[(index) % overviewColors.size],
                    isLast = index == recordGroups.orEmpty().size - 1,
                    onClick = {
                        navigator.navigate(route = buildString {
                            append(HistoryDest.Records.route)
                            append("/$type/${key.navKey}")

                            val authorId = selectedAuthor?.id
                            if (authorId != null) {
                                append("?$ARG_AUTHOR_ID=$authorId")
                            }
                        })
                    }
                )
            }
        }
    }

    editRecordDialog?.let { editRecord ->
        EditRecordDialog(
            editRecord = editRecord,
            onDismiss = {
                editRecordDialog = null
            }
        )
    }
}
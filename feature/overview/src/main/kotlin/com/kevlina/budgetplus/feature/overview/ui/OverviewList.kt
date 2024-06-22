package com.kevlina.budgetplus.feature.overview.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kevlina.budgetplus.core.common.RecordType
import com.kevlina.budgetplus.core.common.nav.ARG_AUTHOR_ID
import com.kevlina.budgetplus.core.common.nav.HistoryDest
import com.kevlina.budgetplus.core.common.nav.Navigator
import com.kevlina.budgetplus.core.common.nav.navKey
import com.kevlina.budgetplus.core.data.remote.Author
import com.kevlina.budgetplus.core.data.remote.Record
import com.kevlina.budgetplus.core.data.remote.User
import com.kevlina.budgetplus.core.theme.LocalAppColors
import com.kevlina.budgetplus.core.ui.AppTheme
import com.kevlina.budgetplus.core.ui.InfiniteCircularProgress
import com.kevlina.budgetplus.core.ui.bubble.BubbleDest
import com.kevlina.budgetplus.core.ui.bubble.BubbleShape
import com.kevlina.budgetplus.core.ui.thenIf
import com.kevlina.budgetplus.feature.overview.OverviewMode
import com.kevlina.budgetplus.feature.record.card.DeleteRecordDialog
import com.kevlina.budgetplus.feature.record.card.EditRecordDialog
import com.kevlina.budgetplus.feature.record.card.RecordCard
import com.kevlina.budgetplus.feature.record.card.RecordCardUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Composable
internal fun OverviewList(
    uiState: OverviewListUiState,
    navigator: Navigator,
    modifier: Modifier = Modifier,
    header: (@Composable () -> Unit)? = null,
) {

    val mode by uiState.mode.collectAsStateWithLifecycle()
    val type by uiState.type.collectAsStateWithLifecycle()
    val selectedAuthor by uiState.selectedAuthor.collectAsStateWithLifecycle()
    val totalPrice by uiState.totalPrice.collectAsStateWithLifecycle()
    val recordList = uiState.recordList.collectAsStateWithLifecycle().value
    val recordGroups by uiState.recordGroups.collectAsStateWithLifecycle()
    val isSoloAuthor by uiState.isSoloAuthor.collectAsStateWithLifecycle()

    var editRecordDialog by remember { mutableStateOf<Record?>(null) }
    var deleteRecordDialog by remember { mutableStateOf<Record?>(null) }

    fun navigateToRecords(category: String) {
        navigator.navigate(route = buildString {
            append(HistoryDest.Records.route)
            append("/$type/${category.navKey}")

            val authorId = selectedAuthor?.id
            if (authorId != null) {
                append("?$ARG_AUTHOR_ID=$authorId")
            }
        })
    }

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

        when {
            recordList == null -> item(
                key = OverviewUiType.Loader.name,
                contentType = OverviewUiType.Loader,
                content = { InfiniteCircularProgress(modifier = Modifier.padding(top = 32.dp)) }
            )

            recordList.isEmpty() -> item(
                key = OverviewUiType.ZeroCase.name,
                contentType = OverviewUiType.ZeroCase,
                content = {
                    OverviewZeroCase(modifier = Modifier.padding(top = 32.dp))
                }
            )

            else -> when (mode) {
                OverviewMode.AllRecords -> itemsIndexed(
                    items = recordList,
                    key = { _, record -> record.id },
                    contentType = { _, _ -> OverviewUiType.Record }
                ) { index, record ->
                    RecordCard(
                        uiState = RecordCardUiState(
                            item = record,
                            formattedPrice = uiState.formatPrice(record.price),
                            isLast = index == recordList.lastIndex,
                            canEdit = uiState.canEditRecord(record),
                            showCategory = true,
                            showAuthor = !isSoloAuthor,
                            onEdit = { editRecordDialog = record },
                            onDuplicate = { uiState.duplicateRecord(record) },
                            onDelete = { deleteRecordDialog = record }
                        ),
                        modifier = Modifier.thenIf(index == 0) {
                            val bubbleShape = with(LocalDensity.current) {
                                BubbleShape.RoundedRect(AppTheme.cornerRadius.toPx())
                            }

                            Modifier
                                .padding(top = 8.dp)
                                .onPlaced {
                                    uiState.highlightTapHint(BubbleDest.OverviewRecordTapHint(
                                        size = it.size,
                                        offset = it.positionInRoot(),
                                        shape = bubbleShape
                                    ))
                                }
                        }
                    )
                }

                OverviewMode.GroupByCategories -> itemsIndexed(
                    items = recordGroups?.keys?.toList().orEmpty(),
                    key = { _, key -> key },
                    contentType = { _, _ -> OverviewUiType.Group }
                ) { index, key ->

                    val groupRecords = recordGroups.orEmpty()[key].orEmpty()
                    val sum = groupRecords.sumOf { it.price }

                    OverviewGroup(
                        modifier = Modifier.thenIf(index == 0) {
                            Modifier.padding(top = 8.dp)
                        },
                        uiState = OverviewGroupUiState(
                            category = key,
                            records = groupRecords,
                            sumPrice = uiState.formatPrice(sum),
                            percentage = remember(sum, totalPrice) { sum / totalPrice },
                            color = overviewColors[index % overviewColors.size],
                            isLast = index == recordGroups.orEmpty().size - 1,
                            onClick = { navigateToRecords(key) }
                        ),
                    )
                }

                OverviewMode.PieChart -> {
                    item(
                        key = OverviewUiType.PieChart.name,
                        contentType = OverviewUiType.PieChart,
                        content = {
                            PieChart(
                                modifier = Modifier.fillMaxSize(),
                                totalPrice = totalPrice,
                                recordGroups = recordGroups.orEmpty(),
                                onClick = ::navigateToRecords
                            )
                        }
                    )
                }
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

@Stable
internal data class OverviewListUiState(
    val mode: StateFlow<OverviewMode>,
    val type: StateFlow<RecordType>,
    val selectedAuthor: StateFlow<User?>,
    val totalPrice: StateFlow<Double>,
    val recordList: StateFlow<List<Record>?>,
    val recordGroups: StateFlow<Map<String, List<Record>>?>,
    val isSoloAuthor: StateFlow<Boolean>,
    val highlightTapHint: (BubbleDest) -> Unit,
    val formatPrice: (Double) -> String,
    val canEditRecord: (Record) -> Boolean,
    val duplicateRecord: (Record) -> Unit,
) {
    companion object {

        private val foodRecords = listOf(
            Record(category = "Food", name = "High Tea", price = 45.3, author = Author(name = "Kevin"), id = "1"),
            Record(category = "Food", name = "Steak", price = 9.3, author = Author(name = "Kevin"), id = "2"),
            Record(category = "Food", name = "Snack", price = 4.7, author = Author(name = "Alina"), id = "3"),
            Record(category = "Food", name = "Mix Vegi rice", price = 12.9, author = Author(name = "Alina"), id = "4"),
        )

        val recordGroupsPreview = mapOf(
            "Loan" to listOf(Record(category = "Loan", price = 433.0, author = Author(name = "Kevin"))),
            "Daily" to listOf(Record(category = "Daily", price = 342.1, author = Author(name = "Alina"))),
            "Utility" to listOf(Record(category = "Utility", price = 132.5, author = Author(name = "Kevin"))),
            "Food" to foodRecords,
        )

        val totalPricePreview = recordGroupsPreview.values
            .sumOf { group -> group.sumOf { it.price } }

        val preview = OverviewListUiState(
            mode = MutableStateFlow(OverviewMode.GroupByCategories),
            type = MutableStateFlow(RecordType.Expense),
            selectedAuthor = MutableStateFlow(User(name = "Kevin")),
            totalPrice = MutableStateFlow(totalPricePreview),
            recordList = MutableStateFlow(foodRecords),
            recordGroups = MutableStateFlow(recordGroupsPreview),
            isSoloAuthor = MutableStateFlow(false),
            highlightTapHint = {},
            formatPrice = { "$$it" },
            canEditRecord = { true },
            duplicateRecord = {}
        )
    }
}

@Preview
@Composable
private fun OverviewList_All_Preview() = AppTheme {
    OverviewList(
        uiState = OverviewListUiState.preview.copy(
            mode = MutableStateFlow(OverviewMode.AllRecords)
        ),
        navigator = Navigator.empty,
        modifier = Modifier.background(LocalAppColors.current.light)
    )
}

@Preview
@Composable
private fun OverviewList_Group_Preview() = AppTheme {
    OverviewList(
        uiState = OverviewListUiState.preview,
        navigator = Navigator.empty,
        modifier = Modifier.background(LocalAppColors.current.light)
    )
}

@Preview
@Composable
private fun OverviewList_PieChart_Preview() = AppTheme {
    OverviewList(
        uiState = OverviewListUiState.preview.copy(
            mode = MutableStateFlow(OverviewMode.PieChart)
        ),
        navigator = Navigator.empty,
        modifier = Modifier.background(LocalAppColors.current.light)
    )
}
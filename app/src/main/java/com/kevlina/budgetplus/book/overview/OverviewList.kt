package com.kevlina.budgetplus.book.overview

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kevlina.budgetplus.book.HistoryDest
import com.kevlina.budgetplus.data.remote.Record
import com.kevlina.budgetplus.data.remote.RecordType
import com.kevlina.budgetplus.utils.Navigator

@Composable
fun OverviewList(
    navigator: Navigator,
    includeHeader: Boolean,
    recordGroups: Map<String, List<Record>>,
    type: RecordType,
    totalPrice: Double,
    onTypeSelected: (RecordType) -> Unit,
) {

    val keys = remember(recordGroups) {
        recordGroups.keys.toList()
    }

    LazyColumn(
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(bottom = 48.dp),
        modifier = Modifier.fillMaxSize()
    ) {

        if (includeHeader) {

            item(
                key = OverviewUiType.Header.name,
                contentType = OverviewUiType.Header,
                content = {
                    OverviewHeader(
                        type = type,
                        totalPrice = totalPrice,
                        isGroupEmpty = keys.isEmpty(),
                        onTypeSelected = onTypeSelected,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            )
        }

        itemsIndexed(
            items = keys,
            key = { _, key -> key },
            contentType = { _, _ -> OverviewUiType.Group }
        ) { index, key ->

            OverviewGroup(
                category = key,
                records = recordGroups[key].orEmpty(),
                totalPrice = totalPrice,
                color = overviewColors[(index) % overviewColors.size],
                isLast = index == recordGroups.size - 1,
                onClick = {
                    navigator.navigate(route = "${HistoryDest.Details.route}/$type/$key")
                }
            )
        }

        if (keys.isEmpty()) {

            item(
                key = OverviewUiType.ZeroCase.name,
                contentType = OverviewUiType.ZeroCase,
                content = { OverviewZeroCase() }
            )
        }
    }
}
package com.kevlina.budgetplus.book.overview

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kevlina.budgetplus.book.HistoryDest
import com.kevlina.budgetplus.book.overview.vm.OverviewViewModel
import com.kevlina.budgetplus.core.common.nav.ARG_AUTHOR_ID
import com.kevlina.budgetplus.core.common.nav.Navigator

@Composable
fun OverviewList(
    navigator: Navigator,
    header: (@Composable () -> Unit)? = null,
) {

    val vm = hiltViewModel<OverviewViewModel>()

    val type by vm.type.collectAsState()
    val selectedAuthor by vm.selectedAuthor.collectAsState()
    val totalPrice by vm.totalPrice.collectAsState()
    val recordGroups by vm.recordGroups.collectAsState()
    val keys = remember(recordGroups) {
        recordGroups.keys.toList()
    }

    LazyColumn(
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(bottom = 48.dp),
        modifier = Modifier.fillMaxSize()
    ) {

        if (header != null) {

            item(
                key = OverviewUiType.Header.name,
                contentType = OverviewUiType.Header,
                content = { header() }
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
                    navigator.navigate(route = buildString {
                        append(HistoryDest.Details.route)
                        append("/$type/$key")

                        val authorId = selectedAuthor?.id
                        if (authorId != null) {
                            append("?$ARG_AUTHOR_ID=$authorId")
                        }
                    })
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
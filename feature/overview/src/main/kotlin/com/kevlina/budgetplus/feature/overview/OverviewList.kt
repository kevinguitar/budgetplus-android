package com.kevlina.budgetplus.feature.overview

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import com.kevlina.budgetplus.core.common.nav.ARG_AUTHOR_ID
import com.kevlina.budgetplus.core.common.nav.HistoryDest
import com.kevlina.budgetplus.core.common.nav.Navigator
import com.kevlina.budgetplus.core.common.nav.navKey
import com.kevlina.budgetplus.core.ui.InfiniteCircularProgress
import com.kevlina.budgetplus.feature.overview.vm.OverviewViewModel

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
        recordGroups?.keys?.toList()
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

        when {
            keys == null -> item(
                key = OverviewUiType.Loader.name,
                contentType = OverviewUiType.Loader,
                content = { InfiniteCircularProgress(modifier = Modifier.padding(top = 32.dp)) }
            )

            keys.isEmpty() -> item(
                key = OverviewUiType.ZeroCase.name,
                contentType = OverviewUiType.ZeroCase,
                content = { OverviewZeroCase() }
            )

            else -> itemsIndexed(
                items = keys,
                key = { _, key -> key },
                contentType = { _, _ -> OverviewUiType.Group }
            ) { index, key ->

                OverviewGroup(
                    category = key,
                    records = recordGroups.orEmpty()[key].orEmpty(),
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
}
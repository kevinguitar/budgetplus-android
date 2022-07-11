package com.kevingt.moneybook.book.overview

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.kevingt.moneybook.R
import com.kevingt.moneybook.book.HistoryDest
import com.kevingt.moneybook.book.overview.vm.OverviewViewModel
import com.kevingt.moneybook.data.remote.Record
import com.kevingt.moneybook.ui.RecordTypeTab
import com.kevingt.moneybook.ui.TopBar
import com.kevingt.moneybook.utils.dollar
import timber.log.Timber

@Composable
fun OverviewScreen(
    navController: NavController,
//    viewModel: OverviewViewModel
) {

    val viewModel = hiltViewModel<OverviewViewModel>()
    Timber.d("Screen overview: ${viewModel.hashCode()}")

    val type by viewModel.type.collectAsState()
    val totalPrice by viewModel.totalPrice.collectAsState()
    val recordGroups by viewModel.recordGroups.collectAsState()

    Column {

        TopBar(title = stringResource(id = R.string.overview_title))

        RecordTypeTab(
            selected = type,
            onTypeSelected = viewModel::setRecordType
        )

        TimePeriodSelector()

        Text(
            text = stringResource(id = R.string.overview_total_price, totalPrice),
            style = MaterialTheme.typography.h6,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(16.dp)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {

            items(recordGroups.keys.toList()) { key ->
                RecordGroup(
                    category = key,
                    records = recordGroups[key].orEmpty(),
                    onClick = {
                        navController.navigate(route = "${HistoryDest.Details.route}/$key")
                    }
                )
            }
        }

    }
}

@Composable
fun RecordGroup(
    category: String,
    records: List<Record>,
    onClick: () -> Unit
) {

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
            .clickable(onClick = onClick)
    ) {
        Text(text = category, modifier = Modifier.weight(1F))
        Text(text = records.sumOf { it.price }.dollar)
    }
}
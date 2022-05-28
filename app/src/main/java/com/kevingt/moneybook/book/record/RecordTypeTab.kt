package com.kevingt.moneybook.book.record

import androidx.compose.foundation.layout.height
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kevingt.moneybook.book.record.vm.RecordViewModel
import com.kevingt.moneybook.data.remote.RecordType

@Composable
fun RecordTypeTab() {

    val viewModel = hiltViewModel<RecordViewModel>()
    val type by viewModel.type.collectAsState()

    TabRow(
        modifier = Modifier.height(56.dp),
        selectedTabIndex = type.ordinal
    ) {

        Tab(
            selected = type == RecordType.Expense,
            onClick = { viewModel.setType(RecordType.Expense) }
        ) {
            Text(text = "Expense")
        }

        Tab(
            selected = type == RecordType.Income,
            onClick = { viewModel.setType(RecordType.Income) }
        ) {
            Text(text = "Income")
        }
    }
}

@Composable
@Preview
private fun RecordTypeTab_Preview() = RecordTypeTab()
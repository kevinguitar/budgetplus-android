package com.kevingt.moneybook.ui

import androidx.compose.foundation.layout.height
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kevingt.moneybook.data.remote.RecordType

@Composable
fun RecordTypeTab(
    selected: RecordType,
    onTypeSelected: (RecordType) -> Unit
) {

    TabRow(
        modifier = Modifier.height(56.dp),
        selectedTabIndex = selected.ordinal
    ) {

        Tab(
            selected = selected == RecordType.Expense,
            onClick = { onTypeSelected(RecordType.Expense) }
        ) {
            Text(text = "Expense")
        }

        Tab(
            selected = selected == RecordType.Income,
            onClick = { onTypeSelected(RecordType.Income) }
        ) {
            Text(text = "Income")
        }
    }
}

@Composable
@Preview
private fun RecordTypeTab_Preview() = RecordTypeTab(selected = RecordType.Expense) {}
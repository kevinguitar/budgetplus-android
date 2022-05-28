package com.kevingt.moneybook.book.record

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kevingt.moneybook.book.record.vm.RecordViewModel
import com.kevingt.moneybook.utils.formatted

@Composable
fun RecordInfo() {

    val viewModel = hiltViewModel<RecordViewModel>()

    val date by viewModel.date.collectAsState()

    var showDatePicker by rememberSaveable { mutableStateOf(false) }

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.padding(16.dp)
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.clickable { showDatePicker = true }
        ) {

            Icon(Icons.Filled.DateRange, contentDescription = "date")

            Text(text = date.formatted)
        }

        CategoriesGrid()


    }

    if (showDatePicker) {

        DatePicker(
            onDatePicked = viewModel::setDate,
            onDismiss = { showDatePicker = false }
        )
    }

}
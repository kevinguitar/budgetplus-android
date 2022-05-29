package com.kevingt.moneybook.book.record

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.kevingt.moneybook.book.record.vm.RecordViewModel
import com.kevingt.moneybook.utils.longFormatted

@Composable
fun RecordInfo(navController: NavController) {

    val viewModel = hiltViewModel<RecordViewModel>()

    val date by viewModel.date.collectAsState()
    val name by viewModel.name.collectAsState()

    var showDatePicker by rememberSaveable { mutableStateOf(false) }

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.padding(16.dp)
    ) {

        RecordTypeTab()

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.clickable { showDatePicker = true }
        ) {

            Icon(Icons.Filled.DateRange, contentDescription = "date")

            Text(text = date.longFormatted)
        }

        CategoriesGrid(navController = navController)

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            Text(text = "Description:")

            TextField(value = name, onValueChange = viewModel::setName)
        }

        Calculator(viewModel = viewModel.calculator)

        Button(
            onClick = { viewModel.record() },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(text = "Add")
        }
    }

    if (showDatePicker) {

        DatePicker(
            onDatePicked = viewModel::setDate,
            onDismiss = { showDatePicker = false }
        )
    }

}
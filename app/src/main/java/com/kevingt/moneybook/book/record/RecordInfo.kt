package com.kevingt.moneybook.book.record

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.kevingt.moneybook.book.record.vm.RecordViewModel
import com.kevingt.moneybook.ui.DatePicker
import com.kevingt.moneybook.ui.DatePickerDialog
import com.kevingt.moneybook.ui.RecordTypeTab

@Composable
fun RecordInfo(navController: NavController) {

    val viewModel = hiltViewModel<RecordViewModel>()

    val type by viewModel.type.collectAsState()
    val date by viewModel.date.collectAsState()
    val name by viewModel.name.collectAsState()

    var showDatePicker by rememberSaveable { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.verticalScroll(rememberScrollState())
    ) {

        RecordTypeTab(selected = type, onTypeSelected = viewModel::setType)

        DatePicker(date = date, onClick = { showDatePicker = true })

        CategoriesGrid(navController = navController)

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {

            Text(text = "Description:")

            TextField(value = name, onValueChange = viewModel::setName)
        }

        Calculator(viewModel = viewModel.calculator)

        Button(
            onClick = {
                viewModel.record()
                focusManager.clearFocus()
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(text = "Add")
        }
    }

    if (showDatePicker) {

        DatePickerDialog(
            onDatePicked = viewModel::setDate,
            onDismiss = { showDatePicker = false }
        )
    }

}
package com.kevingt.moneybook.book.record

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.kevingt.moneybook.book.record.vm.RecordViewModel
import com.kevingt.moneybook.data.remote.Record
import com.kevingt.moneybook.ui.DatePickerDialog
import com.kevingt.moneybook.ui.DatePickerLabel
import com.kevingt.moneybook.ui.RecordTypeTab

@Composable
fun RecordInfo(
    navController: NavController,
    record: Record?
) {

    val viewModel = hiltViewModel<RecordViewModel>()

    if (record != null) {
        viewModel.setEditMode(record)
    }

    val type by viewModel.type.collectAsState()
    val date by viewModel.date.collectAsState()
    val name by viewModel.name.collectAsState()

    var showDatePicker by rememberSaveable { mutableStateOf(false) }

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.verticalScroll(rememberScrollState())
    ) {

        RecordTypeTab(selected = type, onTypeSelected = viewModel::setType)

        DatePickerLabel(date = date, onClick = { showDatePicker = true })

        CategoriesGrid(navController = navController)

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {

            Text(text = "Description:")

            TextField(
                value = name,
                onValueChange = viewModel::setName,
                //TODO: Extract to app-level uikit
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
            )
        }

        Calculator(viewModel = viewModel.calculator)

        RecordActions(navController = navController)
    }

    if (showDatePicker) {

        DatePickerDialog(
            date = date,
            onDatePicked = viewModel::setDate,
            onDismiss = { showDatePicker = false }
        )
    }

}
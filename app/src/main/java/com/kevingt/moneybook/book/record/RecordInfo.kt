package com.kevingt.moneybook.book.record

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.kevingt.moneybook.R
import com.kevingt.moneybook.book.AddDest
import com.kevingt.moneybook.book.record.vm.RecordViewModel
import com.kevingt.moneybook.ui.*

@Composable
fun RecordInfo(navController: NavController) {

    val viewModel = hiltViewModel<RecordViewModel>()

    val type by viewModel.type.collectAsState()
    val date by viewModel.date.collectAsState()
    val name by viewModel.name.collectAsState()
    val category by viewModel.category.collectAsState()

    var showDatePicker by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.verticalScroll(rememberScrollState())
    ) {

        RecordTypeTab(selected = type, onTypeSelected = viewModel::setType)

        DatePickerLabel(date = date, onClick = { showDatePicker = true })

        CategoriesGrid(
            type = type,
            onCategorySelected = viewModel::setCategory,
            selectedCategory = category,
            modifier = Modifier.padding(horizontal = 16.dp),
            actionBtn = CategoriesActionBtn.Edit {
                navController.navigate(route = "${AddDest.EditCategory.route}/$type")
            }
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {

            Text(
                text = stringResource(id = R.string.record_description),
                color = LocalAppColors.current.dark
            )

            AppTextField(
                value = name,
                onValueChange = viewModel::setName,
            )
        }

        Calculator(viewModel = viewModel.calculator)

        AppButton(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            onClick = {
                viewModel.record()
                focusManager.clearFocus()
            },
        ) {
            Text(text = stringResource(id = R.string.cta_add))
        }
    }

    if (showDatePicker) {

        DatePickerDialog(
            date = date,
            onDatePicked = viewModel::setDate,
            onDismiss = { showDatePicker = false }
        )
    }

}
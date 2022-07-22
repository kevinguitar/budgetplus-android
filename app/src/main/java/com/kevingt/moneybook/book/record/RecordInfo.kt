package com.kevingt.moneybook.book.record

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.kevingt.moneybook.R
import com.kevingt.moneybook.book.AddDest
import com.kevingt.moneybook.book.record.vm.RecordViewModel
import com.kevingt.moneybook.data.remote.RecordType
import com.kevingt.moneybook.ui.*
import com.kevingt.moneybook.utils.longFormatted

@Composable
fun RecordInfo(navController: NavController) {

    val viewModel = hiltViewModel<RecordViewModel>()

    val type by viewModel.type.collectAsState()
    val date by viewModel.date.collectAsState()
    val name by viewModel.note.collectAsState()
    val category by viewModel.category.collectAsState()

    var showDatePicker by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.verticalScroll(rememberScrollState())
    ) {

        RecordTypeTab(selected = type, onTypeSelected = viewModel::setType)

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .clickable(onClick = { showDatePicker = true })
        ) {

            Icon(
                painter = painterResource(id = R.drawable.ic_today),
                contentDescription = stringResource(id = R.string.select_date),
                tint = LocalAppColors.current.dark
            )

            Text(
                text = date.longFormatted,
                color = LocalAppColors.current.dark
            )
        }

        CategoriesGrid(
            type = type,
            onCategorySelected = viewModel::setCategory,
            onEditClicked = { navController.navigate(route = "${AddDest.EditCategory.route}/$type") },
            modifier = Modifier.padding(horizontal = 16.dp),
            selectedCategory = category
        )

        AppTextField(
            value = name,
            onValueChange = viewModel::setNote,
            title = stringResource(id = R.string.record_note),
            placeholder = stringResource(
                id = when (type) {
                    RecordType.Expense -> R.string.record_note_placeholder_expense
                    RecordType.Income -> R.string.record_note_placeholder_income
                }
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )

        Calculator(viewModel = viewModel.calculator)

        AppButton(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 8.dp),
            onClick = {
                viewModel.record()
                focusManager.clearFocus()
            },
        ) {
            Text(text = stringResource(id = R.string.cta_add))
        }

        AdsBanner()
    }

    if (showDatePicker) {

        DatePickerDialog(
            date = date,
            onDatePicked = viewModel::setDate,
            onDismiss = { showDatePicker = false }
        )
    }

}
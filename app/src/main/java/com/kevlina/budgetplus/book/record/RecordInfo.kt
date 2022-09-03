package com.kevlina.budgetplus.book.record

import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.kevlina.budgetplus.R
import com.kevlina.budgetplus.book.AddDest
import com.kevlina.budgetplus.book.record.vm.RecordViewModel
import com.kevlina.budgetplus.data.remote.RecordType
import com.kevlina.budgetplus.ui.*
import com.kevlina.budgetplus.utils.longFormatted
import com.kevlina.budgetplus.utils.rippleClick

@Composable
fun RecordInfo(
    navController: NavController,
    modifier: Modifier = Modifier
) {

    val viewModel = hiltViewModel<RecordViewModel>()

    val type by viewModel.type.collectAsState()
    val date by viewModel.date.collectAsState()
    val name by viewModel.note.collectAsState()
    val category by viewModel.category.collectAsState()
    val priceText by viewModel.calculator.priceText.collectAsState()

    var showDatePicker by remember { mutableStateOf(false) }

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
    ) {

        RecordTypeTab(selected = type, onTypeSelected = viewModel::setType)

        CategoriesGrid(
            type = type,
            onCategorySelected = viewModel::setCategory,
            onEditClicked = { navController.navigate(route = "${AddDest.EditCategory.route}/$type") },
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
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(bottom = 16.dp)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.rippleClick { showDatePicker = true }
            ) {

                Icon(
                    painter = painterResource(id = R.drawable.ic_today),
                    contentDescription = stringResource(id = R.string.select_date),
                    tint = LocalAppColors.current.dark
                )

                AppText(text = date.longFormatted)
            }

            AppTextField(
                value = priceText,
                onValueChange = {},
                fontSize = FontSize.Header,
                enabled = false,
                title = "$",
                modifier = Modifier.weight(1F)
            )
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
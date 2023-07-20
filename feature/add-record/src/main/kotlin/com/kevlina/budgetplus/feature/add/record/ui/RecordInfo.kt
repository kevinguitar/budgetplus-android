package com.kevlina.budgetplus.feature.add.record.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kevlina.budgetplus.core.common.R
import com.kevlina.budgetplus.core.common.RecordType
import com.kevlina.budgetplus.core.common.nav.AddDest
import com.kevlina.budgetplus.core.common.nav.Navigator
import com.kevlina.budgetplus.core.ui.DatePickerDialog
import com.kevlina.budgetplus.core.ui.FontSize
import com.kevlina.budgetplus.core.ui.RecordTypeTab
import com.kevlina.budgetplus.core.ui.SingleDatePicker
import com.kevlina.budgetplus.core.ui.TextField
import com.kevlina.budgetplus.core.ui.rippleClick
import com.kevlina.budgetplus.core.ui.thenIf
import com.kevlina.budgetplus.feature.add.record.CalculatorViewModel
import com.kevlina.budgetplus.feature.add.record.RecordViewModel
import com.kevlina.budgetplus.feature.category.pills.CategoriesGrid

@Composable
fun RecordInfo(
    navigator: Navigator,
    scrollable: Boolean,
    modifier: Modifier = Modifier,
) {

    val vm = hiltViewModel<RecordViewModel>()
    val focusManager = LocalFocusManager.current

    val type by vm.type.collectAsStateWithLifecycle()
    val date by vm.date.collectAsStateWithLifecycle()
    val note by vm.note.collectAsStateWithLifecycle()
    val category by vm.category.collectAsStateWithLifecycle()
    val priceText by vm.calculator.priceText.collectAsStateWithLifecycle()

    val scrollState = rememberScrollState()
    var showDatePicker by remember { mutableStateOf(false) }

    if (scrollable) {
        LaunchedEffect(key1 = priceText) {
            if (priceText != CalculatorViewModel.EMPTY_PRICE
                && scrollState.value != scrollState.maxValue
            ) {
                scrollState.animateScrollTo(scrollState.maxValue)
            }
        }
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier.thenIf(scrollable) {
            Modifier.verticalScroll(scrollState)
        }
    ) {

        RecordTypeTab(
            selected = type,
            onTypeSelected = vm::setType,
            modifier = Modifier.padding(top = 16.dp)
        )

        CategoriesGrid(
            type = type,
            onCategorySelected = vm::setCategory,
            onEditClicked = { navigator.navigate(route = "${AddDest.EditCategory.route}/$type") },
            selectedCategory = category,
            modifier = Modifier.fillMaxWidth()
        )

        TextField(
            value = note,
            onValueChange = vm::setNote,
            title = stringResource(id = R.string.record_note),
            placeholder = stringResource(
                id = when (type) {
                    RecordType.Expense -> R.string.record_note_placeholder_expense
                    RecordType.Income -> R.string.record_note_placeholder_income
                }
            ),
            modifier = Modifier.fillMaxWidth(),
            onDone = { focusManager.clearFocus() }
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(bottom = 16.dp)
        ) {

            SingleDatePicker(
                date = date,
                modifier = Modifier
                    .rippleClick { showDatePicker = true }
                    .padding(vertical = 8.dp)
            )

            TextField(
                value = priceText,
                onValueChange = {},
                fontSize = FontSize.Header,
                letterSpacing = 0.5.sp,
                readOnly = true,
                title = "$",
                modifier = Modifier
                    .weight(1F)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = { focusManager.clearFocus() }
                    )
            )
        }
    }

    if (showDatePicker) {

        DatePickerDialog(
            date = date,
            onDatePicked = vm::setDate,
            onDismiss = { showDatePicker = false }
        )
    }

}
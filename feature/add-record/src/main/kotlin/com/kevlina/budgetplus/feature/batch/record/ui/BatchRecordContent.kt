package com.kevlina.budgetplus.feature.batch.record.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kevlina.budgetplus.core.common.R
import com.kevlina.budgetplus.core.common.RecordType
import com.kevlina.budgetplus.core.theme.LocalAppColors
import com.kevlina.budgetplus.core.ui.Button
import com.kevlina.budgetplus.core.ui.FontSize
import com.kevlina.budgetplus.core.ui.RecordTypeTab
import com.kevlina.budgetplus.core.ui.Text
import com.kevlina.budgetplus.core.ui.TextField
import com.kevlina.budgetplus.core.ui.clearFocusSafe
import com.kevlina.budgetplus.core.ui.containerPadding
import com.kevlina.budgetplus.feature.batch.record.BatchRecordViewModel
import com.kevlina.budgetplus.feature.category.pills.CategoriesGrid
import com.kevlina.budgetplus.feature.category.pills.toUiState

@Composable
internal fun BatchRecordContent() {

    val vm = hiltViewModel<BatchRecordViewModel>()
    val focusManager = LocalFocusManager.current

    val type by vm.type.collectAsStateWithLifecycle()
    val note by vm.note.collectAsStateWithLifecycle()
    val currencySymbol by vm.bookRepo.currencySymbol.collectAsStateWithLifecycle()
    val priceText by vm.priceText.collectAsStateWithLifecycle()
    val isBatchButtonEnabled by vm.isBatchButtonEnabled.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .containerPadding()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        RecordTypeTab(
            selected = type,
            onTypeSelected = vm::setType,
            modifier = Modifier.padding(top = 16.dp)
        )

        CategoriesGrid(
            uiState = vm.categoriesVm.toUiState(type = vm.type),
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
            onDone = { focusManager.clearFocusSafe() }
        )

        TextField(
            value = priceText,
            onValueChange = vm::setPriceText,
            fontSize = FontSize.Header,
            title = currencySymbol,
            placeholder = "0",
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        BatchConfigSelector()

        Button(
            onClick = vm::batchRecord,
            enabled = isBatchButtonEnabled,
            modifier = Modifier.padding(top = 16.dp, bottom = 32.dp)
        ) {

            Text(
                text = stringResource(id = R.string.batch_record_cta),
                color = LocalAppColors.current.light,
                fontSize = FontSize.Large,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(vertical = 4.dp, horizontal = 24.dp)
            )
        }
    }
}
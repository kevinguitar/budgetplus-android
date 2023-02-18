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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kevlina.budgetplus.core.common.R
import com.kevlina.budgetplus.core.common.RecordType
import com.kevlina.budgetplus.core.ui.AppButton
import com.kevlina.budgetplus.core.ui.AppText
import com.kevlina.budgetplus.core.ui.AppTextField
import com.kevlina.budgetplus.core.ui.FontSize
import com.kevlina.budgetplus.core.ui.LocalAppColors
import com.kevlina.budgetplus.core.ui.RecordTypeTab
import com.kevlina.budgetplus.feature.add.record.ui.CategoriesGrid
import com.kevlina.budgetplus.feature.batch.record.BatchRecordViewModel

@Composable
internal fun BatchRecordContent() {

    val vm = hiltViewModel<BatchRecordViewModel>()
    val focusManager = LocalFocusManager.current

    val type by vm.type.collectAsState()
    val note by vm.note.collectAsState()
    val category by vm.category.collectAsState()
    val priceText by vm.priceText.collectAsState()
    val isBatchButtonEnabled by vm.isBatchButtonEnabled.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
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
            type = type,
            onCategorySelected = vm::setCategory,
            selectedCategory = category,
            modifier = Modifier.fillMaxWidth()
        )

        AppTextField(
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

        AppTextField(
            value = priceText,
            onValueChange = vm::setPriceText,
            fontSize = FontSize.Header,
            title = "$",
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        BatchConfigSelector()

        AppButton(
            onClick = vm::batchRecord,
            enabled = isBatchButtonEnabled,
            modifier = Modifier.padding(top = 16.dp, bottom = 32.dp)
        ) {

            AppText(
                text = stringResource(id = R.string.batch_record_cta),
                color = LocalAppColors.current.light,
                fontSize = FontSize.Large,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(vertical = 4.dp, horizontal = 24.dp)
            )
        }
    }
}
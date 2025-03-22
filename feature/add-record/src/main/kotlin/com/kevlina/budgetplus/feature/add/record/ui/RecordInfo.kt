package com.kevlina.budgetplus.feature.add.record.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kevlina.budgetplus.core.common.R
import com.kevlina.budgetplus.core.common.RecordType
import com.kevlina.budgetplus.core.theme.LocalAppColors
import com.kevlina.budgetplus.core.ui.AppTheme
import com.kevlina.budgetplus.core.ui.RecordTypeTab
import com.kevlina.budgetplus.core.ui.TextField
import com.kevlina.budgetplus.core.ui.rememberSafeFocusManager
import com.kevlina.budgetplus.core.ui.thenIf
import com.kevlina.budgetplus.feature.category.pills.CategoriesGrid
import com.kevlina.budgetplus.feature.category.pills.CategoriesGridUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Composable
internal fun RecordInfo(
    uiState: RecordInfoUiState,
    modifier: Modifier = Modifier,
) {

    val focusManager = rememberSafeFocusManager()

    val type by uiState.type.collectAsStateWithLifecycle()

    val scrollState = rememberScrollState()

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier.thenIf(uiState.scrollable) {
            Modifier.verticalScroll(scrollState)
        }
    ) {

        RecordTypeTab(
            selected = type,
            onTypeSelected = uiState.setType,
            modifier = Modifier.padding(top = 16.dp)
        )

        CategoriesGrid(
            uiState = uiState.categoriesGridUiState,
            modifier = Modifier.fillMaxWidth()
        )

        TextField(
            state = uiState.note,
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

        DateAndPricing(
            uiState = uiState.dateAndPricingUiState,
            scrollState = scrollState,
            modifier = Modifier.padding(bottom = 16.dp)
        )
    }
}

@Stable
internal data class RecordInfoUiState(
    val type: StateFlow<RecordType>,
    val note: TextFieldState,
    val setType: (RecordType) -> Unit,
    val scrollable: Boolean,
    val categoriesGridUiState: CategoriesGridUiState,
    val dateAndPricingUiState: DateAndPricingUiState,
) {
    companion object {
        val preview = RecordInfoUiState(
            type = MutableStateFlow(RecordType.Expense),
            note = TextFieldState("Some cool daily stuff"),
            setType = {},
            scrollable = true,
            categoriesGridUiState = CategoriesGridUiState.preview,
            dateAndPricingUiState = DateAndPricingUiState.preview
        )
    }
}

@Preview
@Composable
private fun RecordInfo_Preview() = AppTheme {
    RecordInfo(
        uiState = RecordInfoUiState.preview,
        modifier = Modifier
            .background(LocalAppColors.current.light)
            .padding(horizontal = 16.dp)
    )
}
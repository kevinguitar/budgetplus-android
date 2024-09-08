package com.kevlina.budgetplus.feature.add.record.ui

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kevlina.budgetplus.core.theme.LocalAppColors
import com.kevlina.budgetplus.core.ui.AppTheme
import com.kevlina.budgetplus.core.ui.DatePickerDialog
import com.kevlina.budgetplus.core.ui.FontSize
import com.kevlina.budgetplus.core.ui.SingleDatePicker
import com.kevlina.budgetplus.core.ui.TextField
import com.kevlina.budgetplus.core.ui.clickableWithoutRipple
import com.kevlina.budgetplus.core.ui.rippleClick
import com.kevlina.budgetplus.feature.add.record.CalculatorViewModel
import com.kevlina.budgetplus.feature.add.record.RecordDateState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDate

@Composable
internal fun ColumnScope.DateAndPricing(
    uiState: DateAndPricingUiState,
    scrollState: ScrollState,
    modifier: Modifier = Modifier,
) {

    val focusManager = LocalFocusManager.current

    val recordDate by uiState.recordDate.collectAsStateWithLifecycle()
    val currencySymbol by uiState.currencySymbol.collectAsStateWithLifecycle()
    val priceText by uiState.priceText.collectAsStateWithLifecycle()

    var showDatePicker by remember { mutableStateOf(false) }

    if (uiState.scrollable) {
        LaunchedEffect(key1 = priceText) {
            if (priceText != CalculatorViewModel.EMPTY_PRICE) {
                focusManager.clearFocus()
                if (scrollState.value != scrollState.maxValue) {
                    scrollState.animateScrollTo(scrollState.maxValue)
                }
            }
        }
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
    ) {

        SingleDatePicker(
            date = recordDate.date,
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
            title = currencySymbol,
            onTitleClick = uiState.editCurrency,
            modifier = Modifier
                .weight(1F)
                .clickableWithoutRipple { focusManager.clearFocus() }
        )
    }

    if (showDatePicker) {
        DatePickerDialog(
            date = recordDate.date,
            onDatePicked = uiState.setDate,
            onDismiss = { showDatePicker = false }
        )
    }
}

@Immutable
internal class DateAndPricingUiState(
    val recordDate: StateFlow<RecordDateState>,
    val currencySymbol: StateFlow<String>,
    val priceText: StateFlow<String>,
    val scrollable: Boolean,
    val setDate: (LocalDate) -> Unit,
    val editCurrency: () -> Unit,
) {
    companion object {
        val preview = DateAndPricingUiState(
            recordDate = MutableStateFlow(RecordDateState.Now),
            currencySymbol = MutableStateFlow("$"),
            priceText = MutableStateFlow("2344"),
            scrollable = false,
            setDate = {},
            editCurrency = {}
        )
    }
}

@Preview
@Composable
private fun DateAndPricing_Preview() = AppTheme {
    Column {
        DateAndPricing(
            uiState = DateAndPricingUiState.preview,
            scrollState = rememberScrollState(),
            modifier = Modifier
                .background(LocalAppColors.current.light)
                .padding(horizontal = 16.dp)
        )
    }
}
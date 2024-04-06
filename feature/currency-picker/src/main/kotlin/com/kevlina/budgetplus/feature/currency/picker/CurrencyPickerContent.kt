package com.kevlina.budgetplus.feature.currency.picker

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kevlina.budgetplus.core.common.R
import com.kevlina.budgetplus.core.theme.LocalAppColors
import com.kevlina.budgetplus.core.ui.AppTheme
import com.kevlina.budgetplus.core.ui.FontSize
import com.kevlina.budgetplus.core.ui.SearchField
import com.kevlina.budgetplus.core.ui.Text
import com.kevlina.budgetplus.core.ui.rippleClick
import java.util.Currency

@Composable
internal fun CurrencyPickerContent(
    currencies: List<CurrencyUiState>,
    onCurrencyPicked: (CurrencyUiState) -> Unit,
    onSearch: (String) -> Unit,
    modifier: Modifier = Modifier,
) {

    var keyword by rememberSaveable { mutableStateOf("") }

    Column(
        modifier = modifier.fillMaxSize()
    ) {

        SearchField(
            keyword = keyword,
            onKeywordChanged = {
                keyword = it
                onSearch(it)
            },
            hint = stringResource(id = R.string.currency_picker_hint),
            modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 8.dp)
        )

        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 120.dp),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(top = 8.dp, start = 16.dp, end = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            currencies.forEach { currency ->
                item(key = currency.symbol) {
                    CurrencyCard(
                        uiState = currency,
                        onClick = { onCurrencyPicked(currency) }
                    )
                }
            }
        }
    }
}

@Composable
private fun CurrencyCard(
    uiState: CurrencyUiState,
    onClick: () -> Unit,
) {
    val isSelected = uiState.isSelected

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
                .height(120.dp)
                .clip(AppTheme.cardShape)
                .background(if (isSelected) {
                    LocalAppColors.current.dark
                } else {
                    LocalAppColors.current.lightBg
                })
                .rippleClick(onClick = onClick)
                .padding(8.dp)
    ) {
        val textColor = if (isSelected) {
            LocalAppColors.current.light
        } else {
            LocalAppColors.current.dark
        }

        Text(
            text = uiState.symbol,
            fontSize = FontSize.HeaderXLarge,
            fontWeight = FontWeight.Bold,
            color = textColor,
            textAlign = TextAlign.Center
        )

        Text(
            text = uiState.name,
            color = textColor,
            textAlign = TextAlign.Center
        )
    }
}

@Preview
@Composable
private fun CurrencyPickerContent_Preview() = AppTheme {
    CurrencyPickerContent(
        currencies = Currency.getAvailableCurrencies().mapIndexed { index, currency ->
            CurrencyUiState(
                name = currency.displayName,
                currencyCode = currency.currencyCode,
                symbol = currency.symbol,
                isSelected = index == 0
            )
        },
        onCurrencyPicked = {},
        onSearch = {},
        modifier = Modifier.background(LocalAppColors.current.light)
    )
}

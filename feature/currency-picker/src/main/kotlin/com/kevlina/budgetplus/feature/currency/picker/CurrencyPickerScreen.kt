package com.kevlina.budgetplus.feature.currency.picker

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kevlina.budgetplus.core.common.R
import com.kevlina.budgetplus.core.common.nav.Navigator
import com.kevlina.budgetplus.core.ui.AppTheme
import com.kevlina.budgetplus.core.ui.ConfirmDialog
import com.kevlina.budgetplus.core.ui.TopBar

@Composable
fun CurrencyPickerScreen(
    navigator: Navigator,
    vm: CurrencyPickerViewModel = hiltViewModel(),
) {

    val currencies by vm.currencies.collectAsStateWithLifecycle()

    var currencyDisclaimerDialogState by remember { mutableStateOf<CurrencyUiState?>(null) }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        TopBar(
            title = stringResource(id = R.string.currency_picker_title),
            navigateUp = { navigator.navigateUp() },
        )

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .width(AppTheme.maxContentWidth)
                .align(Alignment.CenterHorizontally)
                .weight(1F)
        ) {

            CurrencyPickerContent(
                currencies = currencies,
                onCurrencyPicked = { currency ->
                    if (vm.hasShownCurrencyDisclaimer) {
                        vm.onCurrencyPicked(currency)
                        navigator.navigateUp()
                    } else {
                        currencyDisclaimerDialogState = currency
                    }
                },
                onSearch = vm::onSearch
            )
        }

        currencyDisclaimerDialogState?.let { currency ->
            ConfirmDialog(
                message = stringResource(id = R.string.currency_picker_no_conversion_disclaimer),
                onConfirm = {
                    vm.onCurrencyPicked(currency)
                    currencyDisclaimerDialogState = null
                    navigator.navigateUp()
                },
                onDismiss = { currencyDisclaimerDialogState = null },
            )
        }
    }
}
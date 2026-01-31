package com.kevlina.budgetplus.feature.currency.picker

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import budgetplus.core.common.generated.resources.Res
import budgetplus.core.common.generated.resources.currency_picker_no_conversion_disclaimer
import budgetplus.core.common.generated.resources.currency_picker_title
import com.kevlina.budgetplus.core.common.nav.BookDest
import com.kevlina.budgetplus.core.common.nav.NavController
import com.kevlina.budgetplus.core.theme.LocalAppColors
import com.kevlina.budgetplus.core.ui.ConfirmDialog
import com.kevlina.budgetplus.core.ui.TopBar
import com.kevlina.budgetplus.core.utils.metroViewModel
import org.jetbrains.compose.resources.stringResource

@Composable
fun CurrencyPickerScreen(
    navController: NavController<BookDest>,
    vm: CurrencyPickerViewModel = metroViewModel(),
) {
    val currencies by vm.currencies.collectAsStateWithLifecycle()

    var currencyDisclaimerDialogState by remember { mutableStateOf<CurrencyState?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LocalAppColors.current.light)
    ) {

        TopBar(
            title = stringResource(Res.string.currency_picker_title),
            navigateUp = { navController.navigateUp() },
        )

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1F)
        ) {

            CurrencyPickerContent(
                keyword = vm.keyword,
                currencies = currencies,
                onCurrencyPicked = { currency ->
                    if (vm.hasShownCurrencyDisclaimer) {
                        vm.onCurrencyPicked(currency)
                        navController.navigateUp()
                    } else {
                        currencyDisclaimerDialogState = currency
                    }
                }
            )
        }

        currencyDisclaimerDialogState?.let { currency ->
            ConfirmDialog(
                message = stringResource(Res.string.currency_picker_no_conversion_disclaimer),
                onConfirm = {
                    vm.onCurrencyPicked(currency)
                    currencyDisclaimerDialogState = null
                    navController.navigateUp()
                },
                onDismiss = { currencyDisclaimerDialogState = null },
            )
        }
    }
}
package com.kevlina.budgetplus.feature.currency.picker

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import budgetplus.core.common.generated.resources.Res
import budgetplus.core.common.generated.resources.currency_picker_edit_success
import com.kevlina.budgetplus.core.common.SnackbarSender
import com.kevlina.budgetplus.core.common.di.ViewModelKey
import com.kevlina.budgetplus.core.common.di.ViewModelScope
import com.kevlina.budgetplus.core.data.BookRepo
import com.kevlina.budgetplus.core.data.local.PreferenceHolder
import dev.zacsweers.metro.ContributesIntoMap
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import java.util.Currency
import java.util.Locale

@ViewModelKey(CurrencyPickerViewModel::class)
@ContributesIntoMap(ViewModelScope::class)
class CurrencyPickerViewModel(
    private val bookRepo: BookRepo,
    private val snackbarSender: SnackbarSender,
    preferenceHolder: PreferenceHolder,
) : ViewModel() {

    val keyword = TextFieldState()

    private var hasShownCurrencyDisclaimerCache by preferenceHolder.bindBoolean(false)
    val hasShownCurrencyDisclaimer: Boolean
        get() {
            val current = hasShownCurrencyDisclaimerCache
            hasShownCurrencyDisclaimerCache = true
            return current
        }

    private val currentCurrencyCode = bookRepo.bookState.value?.currencyCode

    private val defaultLocale = Locale.getDefault()
    private val defaultCurrencyCode = Currency.getInstance(defaultLocale).currencyCode

    private val allCurrencies = Currency.getAvailableCurrencies()
        .map { currency ->
            CurrencyState(
                name = currency.getDisplayName(defaultLocale),
                currencyCode = currency.currencyCode,
                symbol = currency.getSymbol(defaultLocale),
                isSelected = currentCurrencyCode == currency.currencyCode
            )
        }
        // Sort by symbol, so it looks nicer from the beginning
        .sortedByDescending { it.symbol }
        // Then place the default currency at the front
        .sortedByDescending { it.currencyCode == defaultCurrencyCode }
        // Then place the selected one (if exists) at the front
        .sortedByDescending { it.isSelected }

    val currencies: StateFlow<List<CurrencyState>>
        field = MutableStateFlow(allCurrencies)

    init {
        snapshotFlow { keyword.text }
            .onEach(::onSearch)
            .launchIn(viewModelScope)
    }

    private fun onSearch(keyword: CharSequence) {
        currencies.value = allCurrencies.filter {
            it.name.contains(keyword, ignoreCase = true) ||
                it.currencyCode.contains(keyword, ignoreCase = true)
        }
    }

    fun onCurrencyPicked(currency: CurrencyState) {
        val bookName = bookRepo.bookState.value?.name ?: return
        viewModelScope.launch {
            snackbarSender.send(getString(Res.string.currency_picker_edit_success, bookName, currency.name))
            bookRepo.updateCurrency(currency.currencyCode)
        }
    }
}
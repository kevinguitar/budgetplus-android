package com.kevlina.budgetplus.feature.currency.picker

import androidx.lifecycle.ViewModel
import com.kevlina.budgetplus.core.common.R
import com.kevlina.budgetplus.core.common.StringProvider
import com.kevlina.budgetplus.core.common.Toaster
import com.kevlina.budgetplus.core.data.BookRepo
import com.kevlina.budgetplus.core.data.local.PreferenceHolder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Currency
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class CurrencyPickerViewModel @Inject constructor(
    private val bookRepo: BookRepo,
    private val toaster: Toaster,
    private val stringProvider: StringProvider,
    preferenceHolder: PreferenceHolder,
) : ViewModel() {

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
            CurrencyUiState(
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

    private val _currencies = MutableStateFlow(allCurrencies)
    val currencies: StateFlow<List<CurrencyUiState>> = _currencies.asStateFlow()

    fun onSearch(keyword: String) {
        _currencies.value = allCurrencies.filter {
            it.name.contains(keyword, ignoreCase = true) ||
                it.currencyCode.contains(keyword, ignoreCase = true)
        }
    }

    fun onCurrencyPicked(currency: CurrencyUiState) {
        val bookName = bookRepo.bookState.value?.name ?: return
        toaster.showMessage(stringProvider[R.string.currency_picker_edit_success, bookName, currency.name])
        bookRepo.updateCurrency(currency.currencyCode)
    }
}
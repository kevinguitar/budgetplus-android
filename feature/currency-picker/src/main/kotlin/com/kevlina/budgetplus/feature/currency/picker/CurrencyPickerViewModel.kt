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

    private val allCurrencies = Currency.getAvailableCurrencies()
        .map { currency ->
            CurrencyUiState(
                name = currency.getDisplayName(Locale.getDefault()),
                currencyCode = currency.currencyCode,
                symbol = currency.getSymbol(Locale.getDefault()),
                isSelected = currentCurrencyCode == currency.currencyCode
            )
        }
        .sortedByDescending { it.symbol }
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
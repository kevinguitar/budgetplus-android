package com.kevlina.budgetplus.feature.currency.picker

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kevlina.budgetplus.core.common.R
import com.kevlina.budgetplus.core.common.SnackbarSender
import com.kevlina.budgetplus.core.common.StringProvider
import com.kevlina.budgetplus.core.common.di.ViewModelKey
import com.kevlina.budgetplus.core.common.di.ViewModelScope
import com.kevlina.budgetplus.core.data.BookRepo
import com.kevlina.budgetplus.core.data.local.PreferenceHolder
import dev.zacsweers.metro.ContributesIntoMap
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.util.Currency
import java.util.Locale

@ViewModelKey(CurrencyPickerViewModel::class)
@ContributesIntoMap(ViewModelScope::class)
class CurrencyPickerViewModel(
    private val bookRepo: BookRepo,
    private val snackbarSender: SnackbarSender,
    private val stringProvider: StringProvider,
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

    private val _currencies = MutableStateFlow(allCurrencies)
    val currencies: StateFlow<List<CurrencyState>> = _currencies.asStateFlow()

    init {
        snapshotFlow { keyword.text }
            .onEach(::onSearch)
            .launchIn(viewModelScope)
    }

    private fun onSearch(keyword: CharSequence) {
        _currencies.value = allCurrencies.filter {
            it.name.contains(keyword, ignoreCase = true) ||
                it.currencyCode.contains(keyword, ignoreCase = true)
        }
    }

    fun onCurrencyPicked(currency: CurrencyState) {
        val bookName = bookRepo.bookState.value?.name ?: return
        snackbarSender.send(stringProvider[R.string.currency_picker_edit_success, bookName, currency.name])
        bookRepo.updateCurrency(currency.currencyCode)
    }
}
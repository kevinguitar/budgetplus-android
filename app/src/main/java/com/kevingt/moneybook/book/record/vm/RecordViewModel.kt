package com.kevingt.moneybook.book.record.vm

import androidx.lifecycle.ViewModel
import com.kevingt.moneybook.data.remote.BookRepo
import com.kevingt.moneybook.data.remote.RecordType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class RecordViewModel @Inject constructor(
    bookRepo: BookRepo
) : ViewModel() {

    private val _type = MutableStateFlow(RecordType.Expense)
    val type: StateFlow<RecordType> = _type.asStateFlow()

    private val _date = MutableStateFlow(LocalDate.now())
    val date: StateFlow<LocalDate> = _date.asStateFlow()

    private val _category = MutableStateFlow<String?>(null)
    val category: StateFlow<String?> = _category.asStateFlow()

    private val _name = MutableStateFlow("")
    val name: StateFlow<String> = _name.asStateFlow()

    private val _priceText = MutableStateFlow("")
    val priceText: StateFlow<String> = _priceText.asStateFlow()

    private val _price = MutableStateFlow(0.0)
    val price: StateFlow<Double> = _price.asStateFlow()

    val expenseCategories = bookRepo.bookState
        .map { it?.expenseCategories.orEmpty() }

    val incomeCategories = bookRepo.bookState
        .map { it?.incomeCategories.orEmpty() }

    fun setType(type: RecordType) {
        _type.value = type
    }

    fun setDate(date: LocalDate) {
        _date.value = date
    }

    fun setCategory(category: String) {
        _category.value = category
    }

    fun setName(name: String) {
        _name.value = name
    }

    fun setPriceText(priceText: String) {
        _priceText.value = priceText
    }
}
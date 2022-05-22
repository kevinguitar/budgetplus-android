package com.kevingt.moneybook.welcome

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kevingt.moneybook.data.remote.BookRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class WelcomeViewModel @Inject constructor(
    bookRepo: BookRepo,
) : ViewModel() {

    init {
        bookRepo.bookState
            .onEach {
                Timber.d(it.toString())
            }
            .launchIn(viewModelScope)
    }
}
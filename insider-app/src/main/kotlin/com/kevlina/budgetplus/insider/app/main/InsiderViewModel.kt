package com.kevlina.budgetplus.insider.app.main

import androidx.lifecycle.ViewModel
import com.kevlina.budgetplus.core.common.SnackbarSender
import com.kevlina.budgetplus.core.common.di.ViewModelKey
import com.kevlina.budgetplus.core.common.di.ViewModelScope
import dev.zacsweers.metro.ContributesIntoMap

@ViewModelKey(InsiderViewModel::class)
@ContributesIntoMap(ViewModelScope::class)
class InsiderViewModel(
    val snackbarSender: SnackbarSender,
) : ViewModel()
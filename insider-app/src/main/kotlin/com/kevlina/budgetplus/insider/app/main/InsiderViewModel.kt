package com.kevlina.budgetplus.insider.app.main

import androidx.lifecycle.ViewModel
import com.kevlina.budgetplus.core.common.SnackbarSender
import dev.zacsweers.metro.Inject

@Inject
class InsiderViewModel(
    val snackbarSender: SnackbarSender,
) : ViewModel()
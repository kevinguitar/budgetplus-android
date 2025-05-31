package com.kevlina.budgetplus.insider.app.main

import androidx.lifecycle.ViewModel
import com.kevlina.budgetplus.core.common.SnackbarSender
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class InsiderViewModel @Inject constructor(
    val snackbarSender: SnackbarSender,

) : ViewModel()
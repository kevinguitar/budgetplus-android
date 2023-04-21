package com.kevlina.budgetplus.core.ui

import javax.inject.Qualifier

/**
 *  Deliver the snackbar event to let BookActivity show it.
 */
interface SnackbarSender {

    fun showSnackbar(snackbarData: SnackbarData)

}

@Qualifier
annotation class Book
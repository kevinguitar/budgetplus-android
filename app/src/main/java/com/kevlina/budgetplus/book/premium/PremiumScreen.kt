package com.kevlina.budgetplus.book.premium

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.kevlina.budgetplus.R
import com.kevlina.budgetplus.ui.InfiniteCircularProgress
import com.kevlina.budgetplus.ui.TopBar
import com.kevlina.budgetplus.utils.Navigator

@Composable
fun PremiumScreen(navigator: Navigator) {

    val viewModel = hiltViewModel<PremiumViewModel>()
    val isPaymentProcessing by viewModel.isPaymentProcessing.collectAsState(initial = false)
    val purchaseDone by viewModel.purchaseDoneFlow.collectAsState(initial = false)

    LaunchedEffect(key1 = purchaseDone) {
        if (purchaseDone) {
            navigator.navigateUp()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        TopBar(
            title = stringResource(id = R.string.premium_unlock),
            navigateBack = navigator::navigateUp
        )

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .weight(1F)
                .fillMaxWidth()
        ) {
            PremiumContent()

            if (isPaymentProcessing) {
                InfiniteCircularProgress()
            }
        }
    }
}
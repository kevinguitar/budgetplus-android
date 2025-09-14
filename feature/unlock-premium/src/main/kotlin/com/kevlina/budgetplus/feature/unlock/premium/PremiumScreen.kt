package com.kevlina.budgetplus.feature.unlock.premium

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.kevlina.budgetplus.core.common.R
import com.kevlina.budgetplus.core.ui.InfiniteCircularProgress
import com.kevlina.budgetplus.core.ui.TopBar

@Composable
fun PremiumScreen(navController: NavController) {

    val vm = hiltViewModel<PremiumViewModel>()
    val premiumPricing by vm.premiumPricing.collectAsStateWithLifecycle()
    val isPaymentProcessing by vm.isPaymentProcessing.collectAsStateWithLifecycle(initialValue = false)
    val purchaseDone by vm.purchaseDoneFlow.collectAsStateWithLifecycle(initialValue = false)

    // Close the screen in case user enters it from the deeplink
    LaunchedEffect(key1 = Unit) {
        if (vm.isPremium.value) {
            navController.navigateUp()
        }
    }

    LaunchedEffect(key1 = purchaseDone) {
        if (purchaseDone) {
            navController.navigateUp()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        TopBar(
            title = stringResource(id = R.string.premium_unlock),
            navigateUp = navController::navigateUp
        )

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .weight(1F)
                .fillMaxWidth()
        ) {
            PremiumContent(
                premiumPricing = premiumPricing,
                getPremium = vm::getPremium
            )

            if (isPaymentProcessing) {
                InfiniteCircularProgress()
            }
        }
    }
}
package com.kevlina.budgetplus.feature.search

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.kevlina.budgetplus.core.common.R
import com.kevlina.budgetplus.core.common.consumeEach
import com.kevlina.budgetplus.core.common.nav.AddDest
import com.kevlina.budgetplus.core.ui.TopBar
import com.kevlina.budgetplus.feature.search.ui.SearchContent
import kotlinx.coroutines.flow.collect

@Composable
fun SearchScreen(
    navController: NavController,
    vm: SearchViewModel,
) {
    LaunchedEffect(vm) {
        vm.state.unlockPremiumEvent
            .consumeEach { navController.navigate(AddDest.UnlockPremium) }
            .collect()
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TopBar(
            title = stringResource(id = R.string.search_title),
            navigateUp = navController::navigateUp,
        )

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1F)
        ) {
            SearchContent(state = vm.state)
        }
    }
}
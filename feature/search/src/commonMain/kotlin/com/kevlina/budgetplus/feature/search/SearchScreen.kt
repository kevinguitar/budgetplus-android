package com.kevlina.budgetplus.feature.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import budgetplus.core.common.generated.resources.Res
import budgetplus.core.common.generated.resources.search_title
import com.kevlina.budgetplus.core.common.consumeEach
import com.kevlina.budgetplus.core.common.nav.BookDest
import com.kevlina.budgetplus.core.common.nav.NavController
import com.kevlina.budgetplus.core.theme.LocalAppColors
import com.kevlina.budgetplus.core.ui.TopBar
import com.kevlina.budgetplus.feature.search.ui.SearchContent
import kotlinx.coroutines.flow.collect
import org.jetbrains.compose.resources.stringResource

@Composable
fun SearchScreen(
    navController: NavController<BookDest>,
    vm: SearchViewModel,
) {
    LaunchedEffect(vm) {
        vm.state.unlockPremiumEvent
            .consumeEach { navController.navigate(BookDest.UnlockPremium) }
            .collect()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LocalAppColors.current.light)
    ) {
        TopBar(
            title = stringResource(Res.string.search_title),
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
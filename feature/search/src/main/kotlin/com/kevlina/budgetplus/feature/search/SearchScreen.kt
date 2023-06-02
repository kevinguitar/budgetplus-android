package com.kevlina.budgetplus.feature.search

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kevlina.budgetplus.core.ads.AdsBanner
import com.kevlina.budgetplus.core.common.R
import com.kevlina.budgetplus.core.common.nav.Navigator
import com.kevlina.budgetplus.core.ui.AppTheme
import com.kevlina.budgetplus.core.ui.TopBar

@Composable
fun SearchScreen(
    navigator: Navigator,
) {

    val vm = hiltViewModel<SearchViewModel>()

    val isPremium by vm.isPremium.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        TopBar(
            title = stringResource(id = R.string.search_title),
            navigateUp = navigator::navigateUp
        )

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .width(AppTheme.maxContentWidth)
                .align(Alignment.CenterHorizontally)
                .weight(1F)
        ) {

            SearchContent()
        }

        if (!isPremium) {
            AdsBanner()
        }
    }
}
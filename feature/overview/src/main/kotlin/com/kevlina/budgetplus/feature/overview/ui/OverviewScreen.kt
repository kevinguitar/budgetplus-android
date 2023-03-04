package com.kevlina.budgetplus.feature.overview.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.List
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.kevlina.budgetplus.core.common.R
import com.kevlina.budgetplus.core.common.nav.Navigator
import com.kevlina.budgetplus.core.ui.AdaptiveScreen
import com.kevlina.budgetplus.core.ui.MenuAction
import com.kevlina.budgetplus.core.ui.TopBar
import com.kevlina.budgetplus.feature.overview.OverviewMode
import com.kevlina.budgetplus.feature.overview.OverviewViewModel

@Composable
fun OverviewScreen(navigator: Navigator) {

    val vm = hiltViewModel<OverviewViewModel>()

    val mode by vm.mode.collectAsState()
    val bookName by vm.bookName.collectAsState()
    val balance by vm.balance.collectAsState()
    val isHideAds by vm.isHideAds.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {

        TopBar(
            title = stringResource(id = R.string.overview_title, bookName.orEmpty()),
            menuActions = {
                MenuAction(
                    imageVector = when (mode) {
                        OverviewMode.AllRecords -> Icons.Rounded.List
                        OverviewMode.GroupByCategories -> Icons.Rounded.BarChart
                    },
                    description = stringResource(id = R.string.overview_details_title),
                    onClick = vm::toggleMode
                )
            }
        )

        AdaptiveScreen(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .weight(1F),
            regularContent = {
                OverviewContent(
                    navigator = navigator,
                    balance = balance,
                    isHideAds = isHideAds,
                )
            },
            wideContent = {
                OverviewContentWide(
                    navigator = navigator,
                    balance = balance,
                    isHideAds = isHideAds,
                )
            }
        )
    }
}

enum class OverviewUiType {
    Header, Record, Group, ZeroCase, Loader
}
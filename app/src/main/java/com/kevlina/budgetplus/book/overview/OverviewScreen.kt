package com.kevlina.budgetplus.book.overview

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.kevlina.budgetplus.R
import com.kevlina.budgetplus.book.HistoryDest
import com.kevlina.budgetplus.book.overview.vm.OverviewViewModel
import com.kevlina.budgetplus.monetize.AdsBanner
import com.kevlina.budgetplus.monetize.AdsMode
import com.kevlina.budgetplus.ui.TopBar

@Composable
fun OverviewScreen(navController: NavController) {

    val viewModel = hiltViewModel<OverviewViewModel>()

    val bookName by viewModel.bookName.collectAsState()
    val totalPrice by viewModel.totalPrice.collectAsState()
    val isHideAds by viewModel.isHideAds.collectAsState()
    val recordGroups by viewModel.recordGroups.collectAsState()
    val keys = recordGroups.keys.toList()

    Column {

        TopBar(title = stringResource(id = R.string.overview_title, bookName.orEmpty()))

        //TODO: Add zerocase
        LazyColumn(modifier = Modifier.weight(1F)) {

            items(
                count = recordGroups.size + 1,
                key = { index -> if (index == 0) "header" else keys[index - 1] },
            ) { index ->

                if (index == 0) {
                    OverviewHeader(viewModel = viewModel)
                } else {
                    val key = keys[index - 1]
                    OverviewGroup(
                        category = key,
                        records = recordGroups[key].orEmpty(),
                        totalPrice = totalPrice,
                        color = overviewColors[(index - 1) % overviewColors.size],
                        isLast = index == recordGroups.size,
                        onClick = {
                            navController.navigate(route = "${HistoryDest.Details.route}/$key")
                        }
                    )
                }
            }
        }

        if (!isHideAds) {
            AdsBanner(mode = AdsMode.Banner)
        }
    }
}
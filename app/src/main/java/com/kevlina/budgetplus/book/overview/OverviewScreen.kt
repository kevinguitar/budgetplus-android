package com.kevlina.budgetplus.book.overview

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.kevlina.budgetplus.R
import com.kevlina.budgetplus.book.HistoryDest
import com.kevlina.budgetplus.book.overview.vm.OverviewViewModel
import com.kevlina.budgetplus.monetize.AdsBanner
import com.kevlina.budgetplus.monetize.AdsMode
import com.kevlina.budgetplus.ui.AppTheme
import com.kevlina.budgetplus.ui.TopBar

@Composable
fun OverviewScreen(navController: NavController) {

    val viewModel = hiltViewModel<OverviewViewModel>()

    val bookName by viewModel.bookName.collectAsState()
    val totalPrice by viewModel.totalPrice.collectAsState()
    val isHideAds by viewModel.isHideAds.collectAsState()
    val recordGroups by viewModel.recordGroups.collectAsState()
    val keys = recordGroups.keys.toList()
    val isGroupEmpty = keys.isEmpty()

    Column {

        TopBar(title = stringResource(id = R.string.overview_title, bookName.orEmpty()))

        LazyColumn(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .width(AppTheme.maxContentWidth)
                .align(Alignment.CenterHorizontally)
                .weight(1F)
        ) {

            item(
                key = OverviewUiType.Header.name,
                contentType = OverviewUiType.Header,
                content = { OverviewHeader(viewModel, isGroupEmpty) }
            )

            itemsIndexed(
                items = keys,
                key = { _, key -> key },
                contentType = { _, _ -> OverviewUiType.Group }
            ) { index, key ->

                OverviewGroup(
                    category = key,
                    records = recordGroups[key].orEmpty(),
                    totalPrice = totalPrice,
                    color = overviewColors[(index) % overviewColors.size],
                    isLast = index == recordGroups.size - 1,
                    onClick = {
                        navController.navigate(route = "${HistoryDest.Details.route}/$key")
                    }
                )
            }

            if (isGroupEmpty) {

                item(
                    key = OverviewUiType.ZeroCase.name,
                    contentType = OverviewUiType.ZeroCase,
                    content = { OverviewZeroCase() }
                )
            }
        }

        if (!isHideAds) {
            AdsBanner(mode = AdsMode.Banner)
        }
    }
}

enum class OverviewUiType {
    Header, Group, ZeroCase
}
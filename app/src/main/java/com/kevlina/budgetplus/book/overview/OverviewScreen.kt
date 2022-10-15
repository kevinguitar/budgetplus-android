package com.kevlina.budgetplus.book.overview

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kevlina.budgetplus.R
import com.kevlina.budgetplus.book.overview.vm.OverviewViewModel
import com.kevlina.budgetplus.monetize.AdsBanner
import com.kevlina.budgetplus.ui.AppTheme
import com.kevlina.budgetplus.ui.TopBar
import com.kevlina.budgetplus.utils.Navigator

@Composable
fun OverviewScreen(navigator: Navigator) {

    val vm = hiltViewModel<OverviewViewModel>()

    val bookName by vm.bookName.collectAsState()
    val type by vm.type.collectAsState()
    val totalPrice by vm.totalPrice.collectAsState()
    val balance by vm.balance.collectAsState()
    val isHideAds by vm.isHideAds.collectAsState()
    val recordGroups by vm.recordGroups.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {

        TopBar(title = stringResource(id = R.string.overview_title, bookName.orEmpty()))

        Box(
            modifier = Modifier
                .width(AppTheme.maxContentWidth)
                .align(Alignment.CenterHorizontally)
                .weight(1F)
        ) {

            OverviewList(
                navigator = navigator,
                recordGroups = recordGroups,
                type = type,
                totalPrice = totalPrice,
                onTypeSelected = vm::setRecordType,
                modifier = Modifier.fillMaxSize()
            )

            BalanceFloatingLabel(
                balance = balance,
                modifier = Modifier
                    .padding(bottom = 16.dp)
                    .align(Alignment.BottomCenter)
            )
        }

        if (!isHideAds) {
            AdsBanner()
        }
    }
}

enum class OverviewUiType {
    Header, Group, ZeroCase
}
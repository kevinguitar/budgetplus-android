package com.kevlina.budgetplus.book.overview

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.kevlina.budgetplus.R
import com.kevlina.budgetplus.book.overview.vm.OverviewViewModel
import com.kevlina.budgetplus.ui.AdaptiveScreen
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

        AdaptiveScreen(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .weight(1F),
            regularContent = {
                OverviewContent(
                    navigator = navigator,
                    type = type,
                    totalPrice = totalPrice,
                    balance = balance,
                    isHideAds = isHideAds,
                    recordGroups = recordGroups,
                    setRecordType = vm::setRecordType
                )
            },
            wideContent = {
                OverviewContentWide(
                    navigator = navigator,
                    type = type,
                    totalPrice = totalPrice,
                    balance = balance,
                    isHideAds = isHideAds,
                    recordGroups = recordGroups,
                    setRecordType = vm::setRecordType
                )
            }
        )
    }
}

enum class OverviewUiType {
    Header, Group, ZeroCase
}
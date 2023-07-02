package com.kevlina.budgetplus.feature.overview.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kevlina.budgetplus.core.common.dollar
import com.kevlina.budgetplus.core.common.nav.Navigator
import com.kevlina.budgetplus.core.ui.RecordTypeTab
import com.kevlina.budgetplus.feature.overview.OverviewViewModel

@Composable
internal fun OverviewHeader(
    vm: OverviewViewModel,
    navigator: Navigator,
    modifier: Modifier,
) {

    val type by vm.type.collectAsStateWithLifecycle()
    val totalPrice by vm.totalPrice.collectAsStateWithLifecycle()
    val balance by vm.balance.collectAsStateWithLifecycle()
    val recordGroups by vm.recordGroups.collectAsStateWithLifecycle()
    val authors by vm.authors.collectAsStateWithLifecycle()
    val selectedAuthor by vm.selectedAuthor.collectAsStateWithLifecycle()

    val totalPriceText = remember(totalPrice) {
        totalPrice.dollar
    }

    val balanceText = remember(balance) {
        balance.dollar
    }

    val isBalanceCardVisible = remember(recordGroups) {
        recordGroups?.isNotEmpty() == true
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {

        RecordTypeTab(
            selected = type,
            onTypeSelected = vm::setRecordType,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        if (authors.size > 1) {
            AuthorSelector(
                authors = authors,
                selectedAuthor = selectedAuthor,
                setAuthor = vm::setAuthor
            )
        }

        TimePeriodSelector(
            vm = vm.timeModel,
            navigator = navigator,
        )

        AnimatedVisibility(
            visible = isBalanceCardVisible,
            enter = expandVertically(expandFrom = Alignment.Top),
            exit = shrinkVertically()
        ) {

            BalanceCard(
                totalPrice = totalPriceText,
                balance = balanceText,
            )
        }
    }
}
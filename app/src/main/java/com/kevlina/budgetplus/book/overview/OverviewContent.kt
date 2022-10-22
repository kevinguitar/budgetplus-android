package com.kevlina.budgetplus.book.overview

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kevlina.budgetplus.data.remote.Record
import com.kevlina.budgetplus.data.remote.RecordType
import com.kevlina.budgetplus.monetize.AdsBanner
import com.kevlina.budgetplus.ui.AppTheme
import com.kevlina.budgetplus.utils.Navigator

@Composable
fun OverviewContent(
    navigator: Navigator,
    type: RecordType,
    totalPrice: Double,
    balance: Double,
    isHideAds: Boolean,
    recordGroups: Map<String, List<Record>>,
    setRecordType: (RecordType) -> Unit,
) {

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(AppTheme.maxContentWidth)
    ) {

        Box(modifier = Modifier.weight(1F)) {

            OverviewList(
                navigator = navigator,
                includeHeader = true,
                recordGroups = recordGroups,
                type = type,
                totalPrice = totalPrice,
                onTypeSelected = setRecordType,
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
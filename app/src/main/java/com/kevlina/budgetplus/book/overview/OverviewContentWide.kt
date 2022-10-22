package com.kevlina.budgetplus.book.overview

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kevlina.budgetplus.data.remote.Record
import com.kevlina.budgetplus.data.remote.RecordType
import com.kevlina.budgetplus.monetize.AdsBanner
import com.kevlina.budgetplus.utils.Navigator

@Composable
fun OverviewContentWide(
    navigator: Navigator,
    type: RecordType,
    totalPrice: Double,
    balance: Double,
    isHideAds: Boolean,
    recordGroups: Map<String, List<Record>>,
    setRecordType: (RecordType) -> Unit,
) {

    val keys = remember(recordGroups) {
        recordGroups.keys.toList()
    }

    Row(
        horizontalArrangement = Arrangement.spacedBy(24.dp),
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp)
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxHeight()
                .weight(1F)
        ) {

            OverviewHeader(
                type = type,
                totalPrice = totalPrice,
                isGroupEmpty = keys.isEmpty(),
                onTypeSelected = setRecordType,
                modifier = Modifier
                    .weight(1F)
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 16.dp)
            )

            if (!isHideAds) {
                AdsBanner()
            }
        }

        Box(modifier = Modifier.weight(1F)) {

            OverviewList(
                navigator = navigator,
                includeHeader = false,
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
    }
}
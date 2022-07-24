package com.kevingt.moneybook.book.overview

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kevingt.moneybook.R
import com.kevingt.moneybook.book.overview.vm.OverviewViewModel
import com.kevingt.moneybook.ui.AppText
import com.kevingt.moneybook.ui.FontSize
import com.kevingt.moneybook.ui.RecordTypeTab
import com.kevingt.moneybook.utils.roundUpPriceText

@Composable
fun OverviewHeader(viewModel: OverviewViewModel) {

    val type by viewModel.type.collectAsState()
    val totalPrice by viewModel.totalPrice.collectAsState()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        RecordTypeTab(
            selected = type,
            onTypeSelected = viewModel::setRecordType
        )

        TimePeriodSelector()

        AppText(
            text = stringResource(id = R.string.overview_total_price, totalPrice.roundUpPriceText),
            fontSize = FontSize.Large,
            fontWeight = FontWeight.SemiBold
        )
    }
}
package com.kevlina.budgetplus.book.overview

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kevlina.budgetplus.R
import com.kevlina.budgetplus.book.overview.vm.OverviewViewModel
import com.kevlina.budgetplus.ui.AppText
import com.kevlina.budgetplus.ui.FontSize
import com.kevlina.budgetplus.ui.RecordTypeTab
import com.kevlina.budgetplus.utils.roundUpPriceText

@Composable
fun OverviewHeader(
    viewModel: OverviewViewModel,
    isGroupEmpty: Boolean
) {

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

        if (!isGroupEmpty) {
            
            AppText(
                text = stringResource(
                    id = R.string.overview_total_price,
                    totalPrice.roundUpPriceText
                ),
                fontSize = FontSize.Large,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
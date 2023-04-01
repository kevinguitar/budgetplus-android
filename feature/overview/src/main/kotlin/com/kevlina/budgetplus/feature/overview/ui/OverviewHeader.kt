package com.kevlina.budgetplus.feature.overview.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kevlina.budgetplus.core.common.R
import com.kevlina.budgetplus.core.common.roundUpPriceText
import com.kevlina.budgetplus.core.ui.FontSize
import com.kevlina.budgetplus.core.ui.RecordTypeTab
import com.kevlina.budgetplus.core.ui.Text
import com.kevlina.budgetplus.feature.overview.OverviewViewModel

@Composable
fun OverviewHeader(modifier: Modifier) {

    val vm = hiltViewModel<OverviewViewModel>()

    val type by vm.type.collectAsStateWithLifecycle()
    val totalPrice by vm.totalPrice.collectAsStateWithLifecycle()
    val recordGroups by vm.recordGroups.collectAsStateWithLifecycle()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {

        RecordTypeTab(
            selected = type,
            onTypeSelected = vm::setRecordType,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        AuthorSelector()

        TimePeriodSelector()

        if (recordGroups?.isNotEmpty() == true) {

            Text(
                text = stringResource(
                    id = R.string.overview_total_price,
                    totalPrice.roundUpPriceText
                ),
                fontSize = FontSize.Large,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}
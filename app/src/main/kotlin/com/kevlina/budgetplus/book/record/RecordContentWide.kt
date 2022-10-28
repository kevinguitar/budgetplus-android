package com.kevlina.budgetplus.book.record

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kevlina.budgetplus.book.record.vm.RecordViewModel
import com.kevlina.budgetplus.core.common.nav.Navigator
import com.kevlina.budgetplus.monetize.AdsBanner

@Composable
fun RecordContentWide(navigator: Navigator) {

    val viewModel = hiltViewModel<RecordViewModel>()
    val isHideAds by viewModel.isHideAds.collectAsState()

    Row(
        horizontalArrangement = Arrangement.spacedBy(24.dp),
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp)
    ) {

        Column(modifier = Modifier.weight(1F)) {

            RecordInfo(
                navigator = navigator,
                scrollable = true,
                modifier = Modifier.weight(1F)
            )

            if (!isHideAds) {
                AdsBanner()
            }
        }

        Calculator(
            viewModel = viewModel.calculator,
            adaptiveButton = true,
            modifier = Modifier
                .weight(1F)
                .fillMaxHeight()
                .padding(vertical = 16.dp)
        )
    }
}
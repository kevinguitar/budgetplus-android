package com.kevlina.budgetplus.book.record

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.kevlina.budgetplus.book.record.vm.RecordViewModel
import com.kevlina.budgetplus.monetize.AdsBanner

@Composable
fun RecordContentWide(navController: NavController) {

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
                navController = navController,
                modifier = Modifier
                    .weight(1F)
                    .verticalScroll(rememberScrollState())
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
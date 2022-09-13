package com.kevlina.budgetplus.book.record

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.kevlina.budgetplus.book.record.vm.RecordViewModel
import com.kevlina.budgetplus.monetize.AdsBanner
import com.kevlina.budgetplus.ui.AppTheme

@Composable
fun RecordContentPacked(navController: NavController) {

    val viewModel = hiltViewModel<RecordViewModel>()
    val isHideAds by viewModel.isHideAds.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(AppTheme.maxContentWidth)
            .verticalScroll(rememberScrollState())
    ) {

        RecordInfo(
            navController = navController,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Calculator(
            viewModel = viewModel.calculator,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .width(360.dp)
                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
        )

        if (!isHideAds) {
            AdsBanner()
        }
    }
}
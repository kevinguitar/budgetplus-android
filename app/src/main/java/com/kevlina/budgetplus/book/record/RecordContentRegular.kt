package com.kevlina.budgetplus.book.record

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import com.kevlina.budgetplus.ui.LocalAppColors

@Composable
fun RecordContentRegular(navController: NavController) {

    val viewModel = hiltViewModel<RecordViewModel>()
    val isHideAds by viewModel.isHideAds.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {

        RecordInfo(
            navController = navController,
            modifier = Modifier
                .weight(1F)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        )

        Calculator(
            viewModel = viewModel.calculator,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .background(LocalAppColors.current.primaryLight.copy(alpha = 0.5F))
                .padding(vertical = 8.dp, horizontal = 32.dp)
        )

        if (!isHideAds) {
            AdsBanner()
        }
    }
}
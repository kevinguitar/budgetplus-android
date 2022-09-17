package com.kevlina.budgetplus.book.record

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kevlina.budgetplus.book.record.vm.CalculatorViewModel
import com.kevlina.budgetplus.book.record.vm.RecordViewModel
import com.kevlina.budgetplus.monetize.AdsBanner
import com.kevlina.budgetplus.ui.AppTheme
import com.kevlina.budgetplus.ui.LocalAppColors
import com.kevlina.budgetplus.utils.Navigator

@Composable
fun RecordContentRegular(navigator: Navigator) {

    val viewModel = hiltViewModel<RecordViewModel>()
    val isHideAds by viewModel.isHideAds.collectAsState()
    val priceText by viewModel.calculator.priceText.collectAsState()
    val infoScrollState = rememberScrollState()

    LaunchedEffect(key1 = priceText) {
        if (priceText != CalculatorViewModel.EMPTY_PRICE
            && infoScrollState.value != infoScrollState.maxValue
        ) {
            infoScrollState.animateScrollTo(infoScrollState.maxValue)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(AppTheme.maxContentWidth)
    ) {

        RecordInfo(
            navigator = navigator,
            modifier = Modifier
                .weight(1F)
                .padding(horizontal = 16.dp)
                .verticalScroll(infoScrollState)
        )

        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .alpha(0.4F)
                .background(color = LocalAppColors.current.dark)
        )

        Calculator(
            viewModel = viewModel.calculator,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .background(LocalAppColors.current.primaryLight)
                .padding(vertical = 8.dp, horizontal = 16.dp)
                .background(
                    color = LocalAppColors.current.light,
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(vertical = 8.dp, horizontal = 16.dp)
        )

        if (!isHideAds) {
            AdsBanner()
        }
    }
}
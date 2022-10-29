package com.kevlina.budgetplus.feature.add.record

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kevlina.budgetplus.core.ads.AdsBanner
import com.kevlina.budgetplus.core.common.nav.Navigator
import com.kevlina.budgetplus.core.ui.AppTheme
import com.kevlina.budgetplus.core.ui.LocalAppColors
import com.kevlina.budgetplus.feature.add.record.vm.RecordViewModel

@Composable
fun RecordContentRegular(navigator: Navigator) {

    val viewModel = hiltViewModel<RecordViewModel>()
    val isHideAds by viewModel.isHideAds.collectAsState()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxHeight()
    ) {

        RecordInfo(
            navigator = navigator,
            scrollable = true,
            modifier = Modifier
                .weight(1F)
                .width(AppTheme.maxContentWidth)
                .padding(horizontal = 16.dp)
        )

        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .alpha(0.4F)
                .background(color = LocalAppColors.current.dark)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(LocalAppColors.current.primaryLight)
        ) {

            Calculator(
                viewModel = viewModel.calculator,
                modifier = Modifier
                    .align(Alignment.Center)
                    .width(AppTheme.maxContentWidth)
                    .padding(vertical = 8.dp, horizontal = 16.dp)
                    .background(
                        color = LocalAppColors.current.light,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(vertical = 8.dp, horizontal = 16.dp)
            )
        }

        if (!isHideAds) {
            AdsBanner()
        }
    }
}
package com.kevlina.budgetplus.feature.batch.record.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.kevlina.budgetplus.core.common.R
import com.kevlina.budgetplus.core.common.nav.Navigator
import com.kevlina.budgetplus.core.ui.AppTheme
import com.kevlina.budgetplus.core.ui.TopBar
import com.kevlina.budgetplus.feature.add.record.ui.DoneAnimator
import com.kevlina.budgetplus.feature.batch.record.BatchRecordViewModel

@Composable
fun BatchRecordScreen(navigator: Navigator) {

    val vm = hiltViewModel<BatchRecordViewModel>()

    Column(modifier = Modifier.fillMaxSize()) {

        TopBar(
            title = stringResource(id = R.string.batch_record_title),
            navigateUp = navigator::navigateUp,
        )

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .width(AppTheme.maxContentWidth)
                .align(Alignment.CenterHorizontally)
                .weight(1F)
        ) {

            BatchRecordContent()

            DoneAnimator(eventTrigger = vm.recordEvent)
        }
    }
}
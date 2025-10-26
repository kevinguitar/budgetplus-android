package com.kevlina.budgetplus.feature.batch.record.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.kevlina.budgetplus.core.common.R
import com.kevlina.budgetplus.core.common.nav.BookDest
import com.kevlina.budgetplus.core.common.nav.NavController
import com.kevlina.budgetplus.core.ui.TopBar
import com.kevlina.budgetplus.feature.add.record.ui.DoneAnimator
import com.kevlina.budgetplus.feature.batch.record.BatchRecordViewModel

@Composable
fun BatchRecordScreen(navController: NavController<BookDest>) {

    val vm = hiltViewModel<BatchRecordViewModel>()

    Column(modifier = Modifier.fillMaxSize()) {

        TopBar(
            title = stringResource(id = R.string.batch_record_title),
            navigateUp = navController::navigateUp,
        )

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1F)
        ) {

            BatchRecordContent()

            DoneAnimator(eventTrigger = vm.recordEvent)
        }
    }
}
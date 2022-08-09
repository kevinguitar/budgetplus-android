package com.kevlina.budgetplus.book.record

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kevlina.budgetplus.R
import com.kevlina.budgetplus.book.record.vm.RecordViewModel
import com.kevlina.budgetplus.ui.AppButton
import com.kevlina.budgetplus.ui.AppText
import com.kevlina.budgetplus.ui.FontSize
import com.kevlina.budgetplus.ui.LocalAppColors
import com.kevlina.budgetplus.utils.thenIf

@Composable
fun ColumnScope.RecordButton(
    viewModel: RecordViewModel,
    applyBottomPadding: Boolean
) {

    val focusManager = LocalFocusManager.current
    val context = LocalContext.current

    AppButton(
        modifier = Modifier
            .align(Alignment.CenterHorizontally)
            .thenIf(applyBottomPadding) { Modifier.padding(bottom = 16.dp) }
            .size(width = 120.dp, height = 48.dp),
        shape = CircleShape,
        onClick = {
            if (viewModel.record()) {
                focusManager.clearFocus()
                viewModel.showFullScreenAdIfNeeded(context)
            }
        },
    ) {

        AppText(
            text = stringResource(id = R.string.cta_add),
            color = LocalAppColors.current.light,
            fontSize = FontSize.SemiLarge,
            fontWeight = FontWeight.Medium
        )
    }
}
package com.kevlina.budgetplus.feature.record.card

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kevlina.budgetplus.core.common.R
import com.kevlina.budgetplus.core.theme.LocalAppColors
import com.kevlina.budgetplus.core.ui.Button
import com.kevlina.budgetplus.core.ui.Text

@Composable
internal fun EditRecordActions(
    onDeleteClicked: () -> Unit,
    isSaveEnabled: Boolean,
    onSaveClicked: () -> Unit,
) {

    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {

        Button(onClick = onDeleteClicked) {
            Text(
                text = stringResource(id = R.string.cta_delete),
                color = LocalAppColors.current.light,
                fontWeight = FontWeight.Medium
            )
        }

        Button(
            enabled = isSaveEnabled,
            onClick = onSaveClicked
        ) {
            Text(
                text = stringResource(id = R.string.cta_save),
                color = LocalAppColors.current.light,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
package com.kevlina.budgetplus.core.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kevlina.budgetplus.core.common.R
import com.kevlina.budgetplus.core.theme.LocalAppColors

@Composable
fun ConfirmDialog(
    message: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    confirmText: String = stringResource(id = R.string.cta_confirm),
    cancelText: String = stringResource(id = R.string.cta_cancel),
) {

    AppDialog(onDismissRequest = onDismiss) {

        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .width(280.dp)
                .wrapContentHeight()
        ) {

            Text(
                text = message,
                fontSize = FontSize.SemiLarge,
                fontWeight = FontWeight.Medium,
                lineHeight = 24.sp
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                Button(onClick = onDismiss) {
                    Text(
                        text = cancelText,
                        color = LocalAppColors.current.light,
                    )
                }

                Button(onClick = onConfirm) {
                    Text(
                        text = confirmText,
                        color = LocalAppColors.current.light,
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun ConfirmDialog_Preview() = AppTheme {
    ConfirmDialog(
        message = "You won't be able to restore the changes. Are you sure you want to delete this book?",
        onConfirm = {},
        onDismiss = {}
    )
}
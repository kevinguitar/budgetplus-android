package com.kevingt.moneybook.ui

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kevingt.moneybook.R

@Composable
fun ConfirmDialog(
    message: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {

    AppDialog(onDismissRequest = onDismiss) {

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .width(280.dp)
                .wrapContentHeight()
        ) {

            AppText(
                text = message,
                fontSize = FontSize.SemiLarge,
                fontWeight = FontWeight.Medium
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {

                AppButton(onClick = onDismiss) {
                    AppText(
                        text = stringResource(id = R.string.cta_cancel),
                        color = LocalAppColors.current.light
                    )
                }

                AppButton(onClick = {
                    onConfirm()
                    onDismiss()
                }) {
                    AppText(
                        text = stringResource(id = R.string.cta_confirm),
                        color = LocalAppColors.current.light
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
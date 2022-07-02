package com.kevingt.moneybook.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
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

            Text(text = message)

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {

                AppButton(onClick = onDismiss) {
                    Text(text = stringResource(id = R.string.cta_cancel))
                }

                AppButton(onClick = {
                    onConfirm()
                    onDismiss()
                }) {
                    Text(text = stringResource(id = R.string.cta_confirm))
                }
            }
        }
    }
}

@Preview
@Composable
private fun ConfirmDialog_Preview() = ConfirmDialog(
    message = "You won't be able to restore the changes. Are you sure you want to delete this book?",
    onConfirm = {},
    onDismiss = {}
)
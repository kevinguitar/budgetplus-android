package com.kevingt.moneybook.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun ConfirmDialog(
    message: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {

    Dialog(onDismissRequest = onDismiss) {

        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .background(color = Color.White, shape = RoundedCornerShape(16.dp))
                .width(280.dp)
                .wrapContentHeight()
                .padding(16.dp)
        ) {

            Text(text = message)

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {

                Button(onClick = onDismiss) {
                    Text(text = "Cancel")
                }

                Button(onClick = {
                    onConfirm()
                    onDismiss()
                }) {
                    Text(text = "Confirm")
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
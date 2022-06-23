package com.kevingt.moneybook.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import kotlinx.coroutines.delay

@Composable
fun InputDialog(
    currentInput: String? = null,
    buttonText: String,
    onButtonClicked: (String) -> Unit,
    onDismiss: () -> Unit,
) {

    var name by remember {
        mutableStateOf(
            TextFieldValue(
                text = currentInput.orEmpty(),
                selection = TextRange(currentInput.orEmpty().length)
            )
        )
    }

    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        delay(100)
        focusRequester.requestFocus()
    }

    Dialog(onDismissRequest = onDismiss) {

        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .background(color = Color.White, shape = RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {

            TextField(
                value = name,
                onValueChange = { name = it },
                modifier = Modifier.focusRequester(focusRequester)
            )

            Button(
                onClick = {
                    onButtonClicked(name.text)
                    onDismiss()
                },
                enabled = name.text.isNotBlank() && name.text != currentInput,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(text = buttonText)
            }
        }
    }
}
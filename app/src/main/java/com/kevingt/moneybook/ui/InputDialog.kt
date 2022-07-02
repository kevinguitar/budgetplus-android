package com.kevingt.moneybook.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
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

    AppDialog(onDismissRequest = onDismiss) {

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {

            AppTextField(
                value = name,
                onValueChange = { name = it },
                modifier = Modifier.focusRequester(focusRequester)
            )

            AppButton(
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
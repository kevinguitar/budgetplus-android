package com.kevlina.budgetplus.core.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp

@Composable
fun InputDialog(
    currentInput: String? = null,
    title: String,
    placeholder: String? = null,
    buttonText: String,
    onButtonClicked: (String) -> Unit,
    onDismiss: () -> Unit,
) {

    var name by remember {
        mutableStateOf(TextFieldValue(
            text = currentInput.orEmpty(),
            selection = TextRange(currentInput.orEmpty().length)
        ))
    }

    val focusRequester = remember { FocusRequester() }

    AppDialog(onDismissRequest = onDismiss) {

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {

            TextField(
                value = name,
                onValueChange = { name = it },
                title = title,
                placeholder = placeholder,
                modifier = Modifier.focusRequester(focusRequester),
                onDone = {
                    if (name.text.isNotBlank() && name.text != currentInput) {
                        onButtonClicked(name.text.trim())
                        onDismiss()
                    }
                }
            )

            Button(
                onClick = {
                    onButtonClicked(name.text.trim())
                    onDismiss()
                },
                enabled = name.text.isNotBlank() && name.text != currentInput,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {

                Text(
                    text = buttonText,
                    color = LocalAppColors.current.light,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}
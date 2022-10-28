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
import kotlinx.coroutines.delay

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

    LaunchedEffect(Unit) {
        delay(100)
        focusRequester.requestFocus()
    }

    AppDialog(onDismissRequest = onDismiss) {

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {

            AppTextField(
                value = name,
                onValueChange = { name = it },
                title = title,
                placeholder = placeholder,
                modifier = Modifier.focusRequester(focusRequester),
                onDone = {
                    if (name.text.isNotBlank() && name.text != currentInput) {
                        onButtonClicked(name.text)
                        onDismiss()
                    }
                }
            )

            AppButton(
                onClick = {
                    onButtonClicked(name.text)
                    onDismiss()
                },
                enabled = name.text.isNotBlank() && name.text != currentInput,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {

                AppText(
                    text = buttonText,
                    color = LocalAppColors.current.light,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
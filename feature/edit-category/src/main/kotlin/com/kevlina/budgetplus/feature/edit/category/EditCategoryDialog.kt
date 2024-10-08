package com.kevlina.budgetplus.feature.edit.category

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kevlina.budgetplus.core.common.R
import com.kevlina.budgetplus.core.theme.LocalAppColors
import com.kevlina.budgetplus.core.ui.AppDialog
import com.kevlina.budgetplus.core.ui.AppTheme
import com.kevlina.budgetplus.core.ui.Button
import com.kevlina.budgetplus.core.ui.Text
import com.kevlina.budgetplus.core.ui.TextField
import kotlinx.serialization.Serializable

@Composable
fun EditCategoryDialog(
    mode: CategoryEditMode,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit,
    onDelete: () -> Unit,
) {

    val currentName = (mode as? CategoryEditMode.Rename)?.currentName.orEmpty()

    var name by remember {
        mutableStateOf(
            TextFieldValue(
                text = currentName,
                selection = TextRange(currentName.length)
            )
        )
    }

    val focusRequester = remember { FocusRequester() }

    AppDialog(onDismissRequest = onDismiss) {

        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {

            TextField(
                value = name,
                onValueChange = { name = it },
                title = stringResource(id = R.string.category_title),
                modifier = Modifier.focusRequester(focusRequester),
                onDone = {
                    if (name.text.isNotBlank() && name.text != currentName) {
                        onConfirm(name.text)
                        onDismiss()
                    }
                }
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {

                if (mode is CategoryEditMode.Rename) {
                    Button(onClick = {
                        onDelete()
                        onDismiss()
                    }) {
                        Text(
                            text = stringResource(id = R.string.cta_delete),
                            color = LocalAppColors.current.light,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Button(
                    onClick = {
                        onConfirm(name.text)
                        onDismiss()
                    },
                    enabled = name.text.isNotBlank() && name.text != currentName,
                ) {
                    val textRes = when (mode) {
                        CategoryEditMode.Add -> R.string.cta_add
                        is CategoryEditMode.Rename -> R.string.cta_rename
                    }
                    Text(
                        text = stringResource(id = textRes),
                        color = LocalAppColors.current.light,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}

@Serializable
sealed class CategoryEditMode {

    data object Add : CategoryEditMode()

    data class Rename(val currentName: String) : CategoryEditMode()

}

@Preview
@Composable
private fun EditCategoryDialog() = AppTheme {
    EditCategoryDialog(
        mode = CategoryEditMode.Rename("My Awesome Category"),
        onConfirm = {},
        onDismiss = {},
        onDelete = {}
    )
}
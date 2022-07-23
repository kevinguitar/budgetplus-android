package com.kevingt.moneybook.book.category

import android.os.Parcelable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kevingt.moneybook.R
import com.kevingt.moneybook.ui.*
import kotlinx.coroutines.delay
import kotlinx.parcelize.Parcelize

@Parcelize
sealed class CategoryEditMode : Parcelable {

    object Add : CategoryEditMode()

    data class Rename(val currentName: String) : CategoryEditMode()

}

@Composable
fun EditCategoryDialog(
    mode: CategoryEditMode,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit,
    onDelete: () -> Unit
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

    LaunchedEffect(Unit) {
        delay(100)
        focusRequester.requestFocus()
    }

    AppDialog(onDismissRequest = onDismiss) {

        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {

            AppTextField(
                value = name,
                onValueChange = { name = it },
                title = stringResource(id = R.string.category_title),
                modifier = Modifier.focusRequester(focusRequester)
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {

                if (mode is CategoryEditMode.Rename) {
                    AppButton(onClick = {
                        onDelete()
                        onDismiss()
                    }) {
                        AppText(
                            text = stringResource(id = R.string.cta_delete),
                            color = LocalAppColors.current.light
                        )
                    }
                }

                AppButton(
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
                    AppText(
                        text = stringResource(id = textRes),
                        color = LocalAppColors.current.light
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun EditCategoryDialog() = EditCategoryDialog(
    mode = CategoryEditMode.Rename("My Awesome Category"),
    onConfirm = {},
    onDismiss = {},
    onDelete = {}
)
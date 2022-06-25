package com.kevingt.moneybook.book.category

import android.os.Parcelable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.kevingt.moneybook.R
import kotlinx.coroutines.delay
import kotlinx.parcelize.Parcelize

sealed class CategoryEditMode : Parcelable {

    @Parcelize
    object Add : CategoryEditMode()

    @Parcelize
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

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {

                if (mode is CategoryEditMode.Rename) {
                    Button(onClick = {
                        onDelete()
                        onDismiss()
                    }) {
                        Text(text = stringResource(id = R.string.cta_delete))
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
                    Text(text = stringResource(id = textRes))
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
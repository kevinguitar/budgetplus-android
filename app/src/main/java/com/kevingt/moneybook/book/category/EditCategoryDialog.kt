package com.kevingt.moneybook.book.category

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

sealed class CategoryEditMode {

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

    val currentName = (mode as? CategoryEditMode.Rename)?.currentName

    var name by rememberSaveable {
        mutableStateOf((mode as? CategoryEditMode.Rename)?.currentName.orEmpty())
    }

    Dialog(onDismissRequest = onDismiss) {

        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .background(color = Color.White, shape = RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {

            TextField(value = name, onValueChange = { name = it })

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {

                if (mode is CategoryEditMode.Rename) {
                    Button(onClick = {
                        onDelete()
                        onDismiss()
                    }) {
                        Text(text = "Delete")
                    }
                }

                Button(
                    onClick = {
                        onConfirm(name)
                        onDismiss()
                    },
                    enabled = name.isNotBlank() && name != currentName,
                ) {
                    val text = when (mode) {
                        CategoryEditMode.Add -> "Add"
                        is CategoryEditMode.Rename -> "Rename"
                    }
                    Text(text = text)
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
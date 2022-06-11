package com.kevingt.moneybook.book.record

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.kevingt.moneybook.book.record.vm.RecordViewModel

@Composable
fun RecordActions(
    navController: NavController,
) {

    val viewModel = hiltViewModel<RecordViewModel>()
    val isEditing by viewModel.isEditing.collectAsState()

    val focusManager = LocalFocusManager.current

    Box(modifier = Modifier.fillMaxWidth()) {

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.align(Alignment.Center)
        ) {

            Button(
                onClick = {
                    viewModel.record()
                    focusManager.clearFocus()
                    if (isEditing) {
                        navController.popBackStack()
                    }
                },
            ) {
                if (isEditing) {
                    Text(text = "Save")
                } else {
                    Text(text = "Add")
                }
            }

            if (isEditing) {
                Button(
                    onClick = {
                        viewModel.deleteRecord()
                        focusManager.clearFocus()
                        navController.popBackStack()
                    },
                ) {
                    Text(text = "Delete")
                }
            }
        }
    }
}
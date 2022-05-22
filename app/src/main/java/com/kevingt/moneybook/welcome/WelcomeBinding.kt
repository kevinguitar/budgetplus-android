package com.kevingt.moneybook.welcome

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kevingt.moneybook.utils.consumeEach
import kotlinx.coroutines.flow.launchIn

@Composable
fun WelcomeBinding(viewModel: WelcomeViewModel) {

    val context = LocalContext.current

    LaunchedEffect(viewModel) {
        viewModel.navigation
            .consumeEach(context)
            .launchIn(this)
    }

    CreateBook(createBook = viewModel::createBook)
}

@Composable
fun CreateBook(createBook: (String) -> Unit) {

    var value by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp)) {

        TextField(
            value = value,
            onValueChange = { value = it }
        )

        Button(onClick = { createBook(value) }) {
            Text(text = "Create")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CreateBook_Preview() = CreateBook(createBook = {})

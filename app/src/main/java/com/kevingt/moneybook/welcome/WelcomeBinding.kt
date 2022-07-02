package com.kevingt.moneybook.welcome

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.kevingt.moneybook.R
import com.kevingt.moneybook.ui.AppButton
import com.kevingt.moneybook.ui.AppTextField
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

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {

        AppTextField(
            value = value,
            onValueChange = { value = it }
        )

        AppButton(
            onClick = { createBook(value) },
            enabled = value.isNotBlank(),
        ) {
            Text(text = stringResource(id = R.string.cta_create))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CreateBook_Preview() = CreateBook(createBook = {})

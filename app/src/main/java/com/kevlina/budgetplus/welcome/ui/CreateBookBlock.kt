package com.kevlina.budgetplus.welcome.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kevlina.budgetplus.R
import com.kevlina.budgetplus.ui.*

@Composable
fun ColumnScope.CreateBookBlock(createBook: (String) -> Unit) {

    var value by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .weight(1F)
    ) {

        Column(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .width(IntrinsicSize.Max)
                .padding(top = 16.dp, end = 24.dp)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {

                Image(
                    painter = painterResource(id = R.drawable.ic_logo_combined),
                    contentDescription = null
                )

                AppText(
                    text = stringResource(id = R.string.app_name),
                    color = LocalAppColors.current.primary,
                )
            }

            Spacer(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(
                        color = LocalAppColors.current.primary,
                        shape = CircleShape
                    )
            )
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(all = 24.dp)
        ) {

            AppText(
                text = stringResource(id = R.string.welcome_create_book_title),
                color = LocalAppColors.current.primary,
                fontSize = FontSize.Large,
                fontWeight = FontWeight.SemiBold,
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {

                AppTextField(
                    value = value,
                    onValueChange = { value = it },
                    title = stringResource(id = R.string.book_name_title),
                    placeholder = stringResource(id = R.string.book_name_placeholder),
                    modifier = Modifier.weight(1F)
                )

                AppButton(
                    onClick = { createBook(value) },
                    enabled = value.isNotBlank(),
                    shape = CircleShape,
                    modifier = Modifier.size(56.dp)
                ) {
                    AppText(
                        text = stringResource(id = R.string.cta_go),
                        color = LocalAppColors.current.light,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CreateBookBlock_Preview() = AppTheme {
    Column {
        CreateBookBlock(createBook = {})
    }
}

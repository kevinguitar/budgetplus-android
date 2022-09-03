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
fun CreateBookBlock(
    modifier: Modifier,
    createBook: (String) -> Unit,
    isWideMode: Boolean = false
) {

    var value by remember { mutableStateOf("") }

    Box(modifier = modifier) {

        Column(
            modifier = Modifier
                .align(
                    if (isWideMode) {
                        Alignment.BottomStart
                    } else {
                        Alignment.TopEnd
                    }
                )
                .width(IntrinsicSize.Max)
                .padding(vertical = 16.dp, horizontal = 24.dp)
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
                .align(
                    if (isWideMode) {
                        Alignment.TopCenter
                    } else {
                        Alignment.BottomCenter
                    }
                )
                .width(AppTheme.maxContentWidth)
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

@Preview(showBackground = true, widthDp = 360, heightDp = 300)
@Composable
private fun CreateBookBlock_Preview() = AppTheme {
    CreateBookBlock(modifier = Modifier.fillMaxSize(), createBook = {})
}

@Preview(showBackground = true, widthDp = 300, heightDp = 360)
@Composable
private fun CreateBookBlockWide_Preview() = AppTheme {
    CreateBookBlock(modifier = Modifier.fillMaxSize(), createBook = {}, isWideMode = true)
}

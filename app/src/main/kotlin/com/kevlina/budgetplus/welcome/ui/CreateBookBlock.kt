package com.kevlina.budgetplus.welcome.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kevlina.budgetplus.R
import com.kevlina.budgetplus.ui.AppButton
import com.kevlina.budgetplus.ui.AppText
import com.kevlina.budgetplus.ui.AppTextField
import com.kevlina.budgetplus.ui.AppTheme
import com.kevlina.budgetplus.ui.FontSize
import com.kevlina.budgetplus.ui.LocalAppColors

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
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
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
                    modifier = Modifier.weight(1F),
                    onDone = {
                        if (value.isNotBlank()) {
                            createBook(value)
                        }
                    }
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
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 400, heightDp = 240)
@Composable
private fun CreateBookBlock_Preview() = AppTheme {
    CreateBookBlock(modifier = Modifier.fillMaxSize(), createBook = {})
}

@Preview(showBackground = true, widthDp = 400, heightDp = 240)
@Composable
private fun CreateBookBlockWide_Preview() = AppTheme {
    CreateBookBlock(modifier = Modifier.fillMaxSize(), createBook = {}, isWideMode = true)
}

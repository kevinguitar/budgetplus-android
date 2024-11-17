package com.kevlina.budgetplus.feature.welcome.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kevlina.budgetplus.core.common.R
import com.kevlina.budgetplus.core.theme.LocalAppColors
import com.kevlina.budgetplus.core.ui.AppTheme
import com.kevlina.budgetplus.core.ui.Button
import com.kevlina.budgetplus.core.ui.FontSize
import com.kevlina.budgetplus.core.ui.Text
import com.kevlina.budgetplus.core.ui.TextField
import com.kevlina.budgetplus.core.ui.thenIf

@Composable
fun CreateBookBlock(
    modifier: Modifier,
    bookName: TextFieldState,
    createBook: () -> Unit,
    isWideMode: Boolean = false,
    applyStatusBarPadding: Boolean = true,
    applyNavBarPadding: Boolean = false,
) {

    Box(
        modifier = modifier
            .background(LocalAppColors.current.light)
            .thenIf(applyStatusBarPadding) { Modifier.statusBarsPadding() }
            .thenIf(applyNavBarPadding) { Modifier.navigationBarsPadding() }
    ) {

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
                .padding(vertical = 24.dp, horizontal = 16.dp)
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

                Text(
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
                .width(AppTheme.containerMaxWidth)
                .padding(vertical = 24.dp, horizontal = 16.dp)
        ) {

            Text(
                text = stringResource(id = R.string.welcome_create_book_title),
                color = LocalAppColors.current.primary,
                fontSize = FontSize.Large,
                fontWeight = FontWeight.SemiBold,
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {

                TextField(
                    state = bookName,
                    title = stringResource(id = R.string.book_name_title),
                    placeholder = stringResource(id = R.string.book_name_placeholder),
                    modifier = Modifier.weight(1F),
                    onDone = {
                        if (bookName.text.isNotBlank()) {
                            createBook()
                        }
                    }
                )

                Button(
                    onClick = createBook,
                    enabled = bookName.text.isNotBlank(),
                    shape = CircleShape,
                    contentPadding = PaddingValues(),
                    modifier = Modifier.size(56.dp)
                ) {

                    Text(
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
    CreateBookBlock(
        modifier = Modifier.fillMaxSize(),
        bookName = rememberTextFieldState("My book"),
        createBook = {}
    )
}

@Preview(showBackground = true, widthDp = 400, heightDp = 240)
@Composable
private fun CreateBookBlockWide_Preview() = AppTheme {
    CreateBookBlock(
        modifier = Modifier.fillMaxSize(),
        bookName = rememberTextFieldState(""),
        createBook = {},
        isWideMode = true
    )
}

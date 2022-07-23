package com.kevingt.moneybook.book.record

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kevingt.moneybook.R
import com.kevingt.moneybook.book.record.vm.BookSelectorViewModel
import com.kevingt.moneybook.book.record.vm.CreateBookBtnState
import com.kevingt.moneybook.ui.*
import com.kevingt.moneybook.utils.rippleClick

@Composable
fun BookSelector() {

    val viewModel = hiltViewModel<BookSelectorViewModel>()

    val bookState by viewModel.book.collectAsState()
    val booksState by viewModel.books.collectAsState()
    val createBookBtnState by viewModel.createBookBtnState.collectAsState()

    var isSelectorShown by remember { mutableStateOf(false) }
    var isBookCreationDialogShown by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth()) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.rippleClick(
                color = LocalAppColors.current.dark,
                onClick = { isSelectorShown = true }
            )
        ) {

            AppText(
                text = bookState?.name.orEmpty(),
                fontSize = FontSize.Header,
                fontWeight = FontWeight.SemiBold,
                color = LocalAppColors.current.light
            )

            Icon(
                imageVector = Icons.Rounded.ArrowDropDown,
                contentDescription = stringResource(id = R.string.book_selection),
                tint = LocalAppColors.current.light
            )
        }

        DropdownMenu(
            expanded = isSelectorShown,
            onDismissRequest = { isSelectorShown = false },
            modifier = Modifier.background(color = LocalAppColors.current.light),
            offset = DpOffset(0.dp, 8.dp)
        ) {

            booksState.orEmpty().forEach { book ->

                DropdownMenuItem(onClick = {
                    viewModel.selectBook(book)
                    isSelectorShown = false
                }) {

                    AppText(
                        text = book.name,
                        color = LocalAppColors.current.primarySemiDark,
                        fontSize = FontSize.SemiLarge
                    )

                    if (bookState?.id == book.id) {
                        Icon(
                            imageVector = Icons.Rounded.Check,
                            contentDescription = null,
                            tint = LocalAppColors.current.primarySemiDark
                        )
                    }
                }
            }

            DropdownMenuDivider()

            DropdownItem(
                name = stringResource(id = R.string.menu_create_book),
                icon = {
                    if (createBookBtnState != CreateBookBtnState.Enabled) {
                        Icon(
                            imageVector = Icons.Rounded.Lock,
                            contentDescription = null,
                            tint = LocalAppColors.current.primarySemiDark,
                            modifier = Modifier
                                .padding(end = 4.dp)
                                .size(20.dp)
                        )
                    }
                },
                onClick = {
                    when (createBookBtnState) {
                        CreateBookBtnState.Enabled -> isBookCreationDialogShown = true
                        CreateBookBtnState.NeedPremium -> viewModel.buyPremium()
                        CreateBookBtnState.ReachedMax -> viewModel.showReachedMaxMessage()
                    }
                    isSelectorShown = false
                }
            )
        }

    }

    if (isBookCreationDialogShown) {

        InputDialog(
            buttonText = stringResource(id = R.string.cta_create),
            title = stringResource(id = R.string.book_name_title),
            onButtonClicked = viewModel::createBook,
            onDismiss = { isBookCreationDialogShown = false }
        )
    }
}
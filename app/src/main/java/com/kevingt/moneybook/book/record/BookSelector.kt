package com.kevingt.moneybook.book.record

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import com.kevingt.moneybook.book.record.vm.BookSelectorViewModel

@Composable
fun BookSelector() {

    val viewModel = hiltViewModel<BookSelectorViewModel>()

    val bookState by viewModel.book.collectAsState()
    val booksState by viewModel.books.collectAsState()

    var isSelectorShown by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth()) {

        Row(verticalAlignment = Alignment.CenterVertically) {

            Text(
                text = bookState?.name.orEmpty(),
                style = MaterialTheme.typography.h5,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )

            if (booksState.orEmpty().size > 1) {

                IconButton(onClick = { isSelectorShown = true }) {
                    Icon(
                        imageVector = Icons.Filled.ArrowDropDown,
                        contentDescription = "Select book",
                        tint = Color.White
                    )
                }
            }
        }

        DropdownMenu(
            expanded = isSelectorShown,
            onDismissRequest = { isSelectorShown = false },
        ) {

            booksState.orEmpty().forEach { book ->

                DropdownMenuItem(onClick = {
                    viewModel.selectBook(book)
                    isSelectorShown = false
                }) {

                    Text(text = book.name)

                    if (bookState?.id == book.id) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = null
                        )
                    }
                }
            }
        }

    }
}
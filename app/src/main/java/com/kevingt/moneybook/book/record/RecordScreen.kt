package com.kevingt.moneybook.book.record

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.kevingt.moneybook.book.menu.BookScreenMenu
import com.kevingt.moneybook.book.record.vm.RecordViewModel
import com.kevingt.moneybook.data.remote.Record
import com.kevingt.moneybook.ui.MenuAction
import com.kevingt.moneybook.ui.TopBar

@Composable
fun RecordScreen(
    navController: NavController,
    record: Record?
) {

    val viewModel = hiltViewModel<RecordViewModel>()

    val context = LocalContext.current

    Column {

        Box {

            TopBar(
                title = null,
                titleContent = { BookSelector() },
                menuActions = listOf(MenuAction(
                    icon = Icons.Filled.Share,
                    description = "Share",
                    onClick = { viewModel.shareJoinLink(context) }
                )),
                dropdownMenu = { BookScreenMenu() }
            )
        }

        RecordInfo(
            navController = navController,
            record = record
        )

    }
}
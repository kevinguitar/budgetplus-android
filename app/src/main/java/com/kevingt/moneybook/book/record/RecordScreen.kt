package com.kevingt.moneybook.book.record

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.kevingt.moneybook.R
import com.kevingt.moneybook.book.bubble.vm.BubbleDest
import com.kevingt.moneybook.book.menu.BookScreenMenu
import com.kevingt.moneybook.book.record.vm.RecordViewModel
import com.kevingt.moneybook.ui.MenuAction
import com.kevingt.moneybook.ui.TopBar

@Composable
fun RecordScreen(navController: NavController) {

    val viewModel = hiltViewModel<RecordViewModel>()

    val context = LocalContext.current

    Column {

        Box {

            TopBar(
                title = null,
                titleContent = { BookSelector() },
                menuActions = {
                    MenuAction(
                        iconRes = R.drawable.ic_invite,
                        description = stringResource(id = R.string.cta_invite),
                        onClick = { viewModel.shareJoinLink(context) },
                        modifier = Modifier.onGloballyPositioned {
                            viewModel.highlightInviteButton(
                                BubbleDest.Invite(
                                    size = it.size,
                                    offset = it.positionInRoot()
                                )
                            )
                        }
                    )
                },
                dropdownMenu = { BookScreenMenu() }
            )
        }

        RecordInfo(navController = navController)

    }
}
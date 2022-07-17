package com.kevingt.moneybook.book.record

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.*
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.kevingt.moneybook.R
import com.kevingt.moneybook.book.menu.BookScreenMenu
import com.kevingt.moneybook.book.record.vm.RecordViewModel
import com.kevingt.moneybook.ui.MenuAction
import com.kevingt.moneybook.ui.TopBar

@Composable
fun RecordScreen(navController: NavController) {

    val viewModel = hiltViewModel<RecordViewModel>()

    val context = LocalContext.current
    var inviteCoordinates by remember { mutableStateOf(0F to 0F) }

    Column {

        Box {

            TopBar(
                title = null,
                titleContent = { BookSelector() },
                menuActions = listOf(MenuAction(
                    iconRes = R.drawable.ic_invite,
                    description = stringResource(id = R.string.cta_invite),
                    onClick = { viewModel.shareJoinLink(context) },
                    onPositioned = { coordinates ->
                        val width = coordinates.size.width
                        val (x1, y) = coordinates.positionInRoot()
                        val x = (x1 + width) / 2
                        inviteCoordinates = x to y
                    }
                )),
                dropdownMenu = { BookScreenMenu() }
            )
        }

        RecordInfo(navController = navController)

    }

    /*TourBubble(
        key = "invite",
        text = "Invite your friends to track together!",
        coordinates = with(LocalDensity.current) {
            inviteCoordinates.first.toDp() to inviteCoordinates.second.toDp()
        }
    )*/
}
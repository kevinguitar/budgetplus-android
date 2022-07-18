package com.kevingt.moneybook.book.record

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
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
//    var inviteBubbleOffset by remember { mutableStateOf(IntOffset.Zero) }

    Column {

        Box {

            TopBar(
                title = null,
                titleContent = { BookSelector() },
                menuActions = listOf(MenuAction(
                    iconRes = R.drawable.ic_invite,
                    description = stringResource(id = R.string.cta_invite),
                    onClick = { viewModel.shareJoinLink(context) },
//                    onPositioned = { inviteBubbleOffset = it.toBubbleOffset() }
                )),
                dropdownMenu = { BookScreenMenu() }
            )
        }

        RecordInfo(navController = navController)

    }

    /*TourBubble(
        key = "invite",
        text = "Invite your friends to track together!",
        bubbleOffset = inviteBubbleOffset
    )*/
}
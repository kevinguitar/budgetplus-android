package com.kevlina.budgetplus.book.record

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.kevlina.budgetplus.R
import com.kevlina.budgetplus.book.bubble.vm.BubbleDest
import com.kevlina.budgetplus.book.menu.BookScreenMenu
import com.kevlina.budgetplus.book.record.vm.RecordViewModel
import com.kevlina.budgetplus.ui.AppTheme
import com.kevlina.budgetplus.ui.MenuAction
import com.kevlina.budgetplus.ui.TopBar

@Composable
fun RecordScreen(navController: NavController) {

    val viewModel = hiltViewModel<RecordViewModel>()

    val context = LocalContext.current

    Column(modifier = Modifier.fillMaxSize()) {

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

        Box(
            modifier = Modifier
                .width(AppTheme.maxContentWidth)
                .align(Alignment.CenterHorizontally)
                .weight(1F)
        ) {

            RecordInfo(navController = navController)

            DoneAnimator()
        }

    }
}
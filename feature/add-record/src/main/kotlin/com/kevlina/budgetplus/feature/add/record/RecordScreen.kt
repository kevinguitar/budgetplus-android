package com.kevlina.budgetplus.feature.add.record

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.GroupAdd
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.kevlina.budgetplus.core.common.R
import com.kevlina.budgetplus.core.common.nav.Navigator
import com.kevlina.budgetplus.core.ui.AdaptiveScreen
import com.kevlina.budgetplus.core.ui.MenuAction
import com.kevlina.budgetplus.core.ui.TopBar
import com.kevlina.budgetplus.core.ui.bubble.BubbleDest
import com.kevlina.budgetplus.feature.add.record.vm.RecordViewModel
import com.kevlina.budgetplus.feature.menu.BookScreenMenu

@Composable
fun RecordScreen(navigator: Navigator) {

    val viewModel = hiltViewModel<RecordViewModel>()
    val context = LocalContext.current

    Column(modifier = Modifier.fillMaxSize()) {

        Box {

            TopBar(
                title = null,
                titleContent = { BookSelector(navigator) },
                menuActions = {
                    MenuAction(
                        imageVector = Icons.Rounded.GroupAdd,
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
                dropdownMenu = { BookScreenMenu(navigator) }
            )
        }

        AdaptiveScreen(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .weight(1F),
            regularContent = {
                RecordContentRegular(navigator = navigator)
            },
            wideContent = {
                RecordContentWide(navigator = navigator)
            },
            packedContent = {
                RecordContentPacked(navigator = navigator)
            },
            extraContent = {
                DoneAnimator()
            }
        )
    }
}
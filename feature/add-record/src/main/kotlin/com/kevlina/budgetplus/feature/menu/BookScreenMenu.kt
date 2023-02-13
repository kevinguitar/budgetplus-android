package com.kevlina.budgetplus.feature.menu

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.kevlina.budgetplus.core.common.R
import com.kevlina.budgetplus.core.common.nav.AddDest
import com.kevlina.budgetplus.core.common.nav.Navigator
import com.kevlina.budgetplus.core.common.nav.consume
import com.kevlina.budgetplus.core.ui.AppText
import com.kevlina.budgetplus.core.ui.ConfirmDialog
import com.kevlina.budgetplus.core.ui.DropdownItem
import com.kevlina.budgetplus.core.ui.DropdownMenu
import com.kevlina.budgetplus.core.ui.DropdownMenuDivider
import com.kevlina.budgetplus.core.ui.DropdownMenuItem
import com.kevlina.budgetplus.core.ui.FontSize
import com.kevlina.budgetplus.core.ui.InputDialog
import com.kevlina.budgetplus.core.ui.LocalAppColors
import com.kevlina.budgetplus.core.ui.MenuAction
import com.kevlina.budgetplus.feature.member.MembersDialog
import kotlinx.coroutines.flow.launchIn

@Composable
fun BookScreenMenu(
    navigator: Navigator,
    showMembers: Boolean,
) {

    val viewModel = hiltViewModel<BookMenuViewModel>()

    val context = LocalContext.current

    LaunchedEffect(viewModel) {
        viewModel.navigation
            .consume(context)
            .launchIn(this)
    }

    val isBookOwner by viewModel.isBookOwner.collectAsState()
    val isPremium by viewModel.isPremium.collectAsState()

    val icPremium by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.ic_premium))

    var isMenuExpanded by remember { mutableStateOf(false) }
    var isRenameUserDialogShown by remember { mutableStateOf(false) }
    var isRenameBookDialogShown by remember { mutableStateOf(false) }
    var isMembersDialogShown by rememberSaveable { mutableStateOf(showMembers) }
    var isDeleteOrLeaveDialogShown by remember { mutableStateOf(false) }

    Box {

        MenuAction(
            imageVector = Icons.Rounded.MoreVert,
            description = stringResource(id = R.string.menu_open),
            onClick = { isMenuExpanded = true }
        )

        DropdownMenu(
            expanded = isMenuExpanded,
            onDismissRequest = { isMenuExpanded = false },
            offset = DpOffset(x = 4.dp, y = 0.dp),
            modifier = Modifier.background(color = LocalAppColors.current.light)
        ) {

            if (!isPremium) {
                DropdownMenuItem(onClick = {
                    isMenuExpanded = false
                    navigator.navigate(AddDest.UnlockPremium.route)
                }) {

                    Row(verticalAlignment = Alignment.CenterVertically) {

                        AppText(
                            text = stringResource(id = R.string.premium_hide_ads),
                            color = LocalAppColors.current.primarySemiDark,
                            fontSize = FontSize.SemiLarge
                        )

                        LottieAnimation(
                            composition = icPremium,
                            iterations = LottieConstants.IterateForever,
                            modifier = Modifier
                                .padding(horizontal = 8.dp)
                                .size(24.dp)
                        )
                    }
                }
            }

            DropdownItem(name = stringResource(id = R.string.menu_rename_user)) {
                isMenuExpanded = false
                isRenameUserDialogShown = true
            }

            DropdownItem(name = stringResource(id = R.string.menu_rename_book)) {
                isMenuExpanded = false
                isRenameBookDialogShown = true
            }

            DropdownItem(name = stringResource(id = R.string.menu_view_members)) {
                isMenuExpanded = false
                isMembersDialogShown = true
            }

            DropdownItem(name = stringResource(id = R.string.menu_logout)) {
                isMenuExpanded = false
                viewModel.logout()
            }

            DropdownMenuDivider()

            DropdownItem(
                name = stringResource(
                    id = if (isBookOwner) {
                        R.string.menu_delete_book
                    } else {
                        R.string.menu_leave_book
                    }
                )
            ) {
                isMenuExpanded = false
                isDeleteOrLeaveDialogShown = true
            }
        }
    }

    if (isRenameUserDialogShown) {

        InputDialog(
            currentInput = viewModel.currentUsername,
            title = stringResource(id = R.string.username_title),
            buttonText = stringResource(id = R.string.cta_rename),
            onButtonClicked = viewModel::renameUser,
            onDismiss = { isRenameUserDialogShown = false }
        )
    }

    if (isRenameBookDialogShown) {

        InputDialog(
            currentInput = viewModel.currentBookName,
            title = stringResource(id = R.string.book_name_title),
            buttonText = stringResource(id = R.string.cta_rename),
            onButtonClicked = viewModel::renameBook,
            onDismiss = { isRenameBookDialogShown = false }
        )
    }

    if (isMembersDialogShown) {

        MembersDialog(
            onDismiss = { isMembersDialogShown = false }
        )
    }

    if (isDeleteOrLeaveDialogShown) {

        ConfirmDialog(
            message = stringResource(
                id = if (isBookOwner) R.string.menu_confirm_delete else R.string.menu_confirm_leave,
                viewModel.currentBookName.orEmpty()
            ),
            onConfirm = {
                viewModel.deleteOrLeave()
                isDeleteOrLeaveDialogShown = false
            },
            onDismiss = { isDeleteOrLeaveDialogShown = false }
        )
    }
}
package com.kevlina.budgetplus.book.menu

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.kevlina.budgetplus.R
import com.kevlina.budgetplus.book.AddDest
import com.kevlina.budgetplus.book.member.MembersDialog
import com.kevlina.budgetplus.book.menu.vm.BookMenuViewModel
import com.kevlina.budgetplus.ui.*
import com.kevlina.budgetplus.utils.consume
import kotlinx.coroutines.flow.launchIn

@Composable
fun BookScreenMenu(navController: NavController) {

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
    var isMembersDialogShown by remember { mutableStateOf(false) }
    var isDeleteOrLeaveDialogShown by remember { mutableStateOf(false) }

    Box {
        IconButton(onClick = { isMenuExpanded = true }) {
            Icon(
                imageVector = Icons.Rounded.MoreVert,
                contentDescription = stringResource(id = R.string.menu_open),
                tint = LocalAppColors.current.light
            )
        }

        DropdownMenu(
            expanded = isMenuExpanded,
            onDismissRequest = { isMenuExpanded = false },
            offset = DpOffset(x = 4.dp, y = 0.dp),
            modifier = Modifier.background(color = LocalAppColors.current.light)
        ) {

            if (!isPremium) {
                DropdownMenuItem(onClick = {
                    isMenuExpanded = false
                    navController.navigate(AddDest.UnlockPremium.route)
                }) {

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
            onConfirm = { viewModel.deleteOrLeave() },
            onDismiss = { isDeleteOrLeaveDialogShown = false }
        )
    }
}
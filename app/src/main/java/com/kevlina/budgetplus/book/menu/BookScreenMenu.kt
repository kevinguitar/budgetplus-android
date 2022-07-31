package com.kevlina.budgetplus.book.menu

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material.DropdownMenu
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
import com.kevlina.budgetplus.R
import com.kevlina.budgetplus.book.member.MembersDialog
import com.kevlina.budgetplus.book.menu.vm.BookMenuViewModel
import com.kevlina.budgetplus.ui.*
import com.kevlina.budgetplus.utils.consumeEach
import kotlinx.coroutines.flow.launchIn

@Composable
fun BookScreenMenu() {

    val viewModel = hiltViewModel<BookMenuViewModel>()

    val context = LocalContext.current

    LaunchedEffect(viewModel) {
        viewModel.navigation
            .consumeEach(context)
            .launchIn(this)
    }

    val isBookOwner by viewModel.isBookOwner.collectAsState()

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

            DropdownItem(name = stringResource(id = R.string.menu_rename_user)) {
                isRenameUserDialogShown = true
                isMenuExpanded = false
            }

            DropdownItem(name = stringResource(id = R.string.menu_rename_book)) {
                isRenameBookDialogShown = true
                isMenuExpanded = false
            }

            DropdownItem(name = stringResource(id = R.string.menu_view_members)) {
                isMembersDialogShown = true
                isMenuExpanded = false
            }

            DropdownItem(name = stringResource(id = R.string.menu_logout)) {
                viewModel.logout()
            }

            DropdownMenuDivider()

            DropdownItem(
                name = stringResource(id = if (isBookOwner) R.string.cta_delete else R.string.cta_leave)
            ) {
                isDeleteOrLeaveDialogShown = true
                isMenuExpanded = false
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
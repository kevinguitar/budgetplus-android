package com.kevingt.moneybook.book.menu

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kevingt.moneybook.book.member.MembersDialog
import com.kevingt.moneybook.book.menu.vm.BookMenuViewModel
import com.kevingt.moneybook.ui.ConfirmDialog
import com.kevingt.moneybook.ui.InputDialog
import com.kevingt.moneybook.utils.consumeEach
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
                imageVector = Icons.Filled.MoreVert,
                contentDescription = "Menu",
                tint = Color.White
            )
        }

        DropdownMenu(
            expanded = isMenuExpanded,
            onDismissRequest = { isMenuExpanded = false },
            offset = DpOffset(x = 4.dp, y = 0.dp)
        ) {

            DropdownItem(name = "Rename My User") {
                isRenameUserDialogShown = true
                isMenuExpanded = false
            }

            DropdownItem(name = "Rename Book") {
                isRenameBookDialogShown = true
                isMenuExpanded = false
            }

            DropdownItem(name = "View Members") {
                isMembersDialogShown = true
                isMenuExpanded = false
            }

            DropdownItem(name = "Create New Book") {

            }

            DropdownItem(name = "Logout") {
                viewModel.logout()
            }

            Divider(modifier = Modifier.padding(vertical = 2.dp))

            DropdownItem(
                name = if (isBookOwner) "Delete" else "Leave",
                isDangerous = true
            ) {
                isDeleteOrLeaveDialogShown = true
            }
        }
    }

    if (isRenameUserDialogShown) {
        InputDialog(
            currentInput = viewModel.currentUsername,
            buttonText = "Rename",
            onButtonClicked = viewModel::renameUser,
            onDismiss = { isRenameUserDialogShown = false }
        )
    }

    if (isRenameBookDialogShown) {
        InputDialog(
            currentInput = viewModel.currentBookName,
            buttonText = "Rename",
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
            message = """
                Are you sure you want to ${if (isBookOwner) "delete" else "leave"} 
                ${viewModel.currentBookName}?
            """.trimIndent(),
            onConfirm = { viewModel.deleteOrLeave() },
            onDismiss = { isDeleteOrLeaveDialogShown = false }
        )
    }
}

@Composable
private fun DropdownItem(
    name: String,
    isDangerous: Boolean = false,
    onClick: () -> Unit
) {

    DropdownMenuItem(onClick = onClick) {
        Text(
            text = name,
            color = if (isDangerous) Color.Red else Color.Unspecified
        )
    }
}
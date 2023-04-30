package com.kevlina.budgetplus.feature.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.EditNote
import androidx.compose.material.icons.rounded.ExitToApp
import androidx.compose.material.icons.rounded.ForwardToInbox
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material.icons.rounded.Logout
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.SupervisedUserCircle
import androidx.compose.material.icons.rounded.Vibration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kevlina.budgetplus.core.common.R
import com.kevlina.budgetplus.core.common.nav.AddDest
import com.kevlina.budgetplus.core.common.nav.Navigator
import com.kevlina.budgetplus.core.common.nav.consume
import com.kevlina.budgetplus.core.ui.ConfirmDialog
import com.kevlina.budgetplus.core.ui.InputDialog
import com.kevlina.budgetplus.core.ui.Switch
import com.kevlina.budgetplus.feature.settings.member.MembersDialog
import kotlinx.coroutines.flow.launchIn

private const val VIBRATE_SWITCH_SCALE = 0.7F

@Composable
internal fun SettingsContent(
    navigator: Navigator,
    showMembers: Boolean,
) {

    val vm = hiltViewModel<SettingsViewModel>()

    val context = LocalContext.current
    LaunchedEffect(vm) {
        vm.navigation
            .consume(context)
            .launchIn(this)
    }

    val isBookOwner by vm.isBookOwner.collectAsStateWithLifecycle()
    val isPremium by vm.isPremium.collectAsStateWithLifecycle()
    val vibrateOnInput by vm.vibrator.vibrateOnInput.collectAsStateWithLifecycle()

    var isRenameUserDialogShown by remember { mutableStateOf(false) }
    var isRenameBookDialogShown by remember { mutableStateOf(false) }
    var isMembersDialogShown by rememberSaveable { mutableStateOf(showMembers) }
    var isDeleteOrLeaveDialogShown by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {

        // Premium section
        SettingsItem(
            text = stringResource(id = R.string.batch_record_title),
            showCrownAnimation = true,
            roundTop = true,
            roundBottom = isPremium,
            onClick = {
                vm.trackBatchRecordClicked()
                if (isPremium) {
                    navigator.navigate(AddDest.BatchRecord.route)
                } else {
                    navigator.navigate(AddDest.UnlockPremium.route)
                }
            }
        )

        if (!isPremium) {
            SettingsItem(
                text = stringResource(id = R.string.premium_hide_ads),
                showCrownAnimation = true,
                roundBottom = true,
                onClick = { navigator.navigate(AddDest.UnlockPremium.route) }
            )
        }

        // Content section
        SettingsItem(
            text = stringResource(id = R.string.settings_rename_user),
            icon = Icons.Rounded.AccountCircle,
            roundTop = true,
            onClick = { isRenameUserDialogShown = true }
        )

        SettingsItem(
            text = stringResource(id = R.string.settings_rename_book),
            icon = Icons.Rounded.EditNote,
            onClick = { isRenameBookDialogShown = true }
        )

        SettingsItem(
            text = stringResource(id = R.string.settings_view_members),
            icon = Icons.Rounded.SupervisedUserCircle,
            roundBottom = true,
            onClick = { isMembersDialogShown = true }
        )

        // General section
        if (vm.canSelectLanguage) {
            SettingsItem(
                text = stringResource(id = R.string.settings_language),
                icon = Icons.Rounded.Language,
                roundTop = true,
                onClick = { vm.openLanguageSettings(context) }
            )
        }

        SettingsItem(
            text = stringResource(id = R.string.settings_input_vibration),
            icon = Icons.Rounded.Vibration,
            roundTop = !vm.canSelectLanguage,
            verticalPadding = 4.dp,
            action = {
                Switch(
                    checked = vibrateOnInput,
                    onCheckedChange = { vm.vibrator.toggleVibrateOnInput() },
                    modifier = Modifier
                        .padding(end = 10.dp)
                        .scale(VIBRATE_SWITCH_SCALE)
                )
            },
            onClick = vm.vibrator::toggleVibrateOnInput
        )

        SettingsItem(
            text = stringResource(id = R.string.settings_share_app),
            icon = Icons.Rounded.Share,
            onClick = { vm.share(context) }
        )

        SettingsItem(
            text = stringResource(id = R.string.settings_rate_us),
            icon = Icons.Rounded.Star,
            onClick = { vm.rateUs(context) }
        )

        SettingsItem(
            text = stringResource(id = R.string.settings_contact_us),
            icon = Icons.Rounded.ForwardToInbox,
            roundBottom = true,
            onClick = { vm.contactUs(context) }
        )

        // Danger section
        SettingsItem(
            text = stringResource(id = if (isBookOwner) {
                R.string.settings_delete_book
            } else {
                R.string.settings_leave_book
            }),
            icon = if (isBookOwner) {
                Icons.Rounded.Delete
            } else {
                Icons.Rounded.ExitToApp
            },
            roundTop = true,
            onClick = { isDeleteOrLeaveDialogShown = true }
        )

        SettingsItem(
            text = stringResource(id = R.string.settings_logout),
            icon = Icons.Rounded.Logout,
            roundBottom = true,
            onClick = vm::logout
        )
    }

    if (isRenameUserDialogShown) {

        InputDialog(
            currentInput = vm.currentUsername,
            title = stringResource(id = R.string.username_title),
            buttonText = stringResource(id = R.string.cta_rename),
            onButtonClicked = vm::renameUser,
            onDismiss = { isRenameUserDialogShown = false }
        )
    }

    if (isRenameBookDialogShown) {

        InputDialog(
            currentInput = vm.currentBookName,
            title = stringResource(id = R.string.book_name_title),
            buttonText = stringResource(id = R.string.cta_rename),
            onButtonClicked = vm::renameBook,
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
                id = if (isBookOwner) R.string.settings_confirm_delete else R.string.settings_confirm_leave,
                vm.currentBookName.orEmpty()
            ),
            onConfirm = {
                vm.deleteOrLeave()
                isDeleteOrLeaveDialogShown = false
            },
            onDismiss = { isDeleteOrLeaveDialogShown = false }
        )
    }
}
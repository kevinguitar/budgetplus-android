package com.kevlina.budgetplus.feature.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.DirectionsRun
import androidx.compose.material.icons.automirrored.rounded.ForwardToInbox
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.automirrored.rounded.ShowChart
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.ColorLens
import androidx.compose.material.icons.rounded.CurrencyExchange
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.EditNote
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material.icons.rounded.PrivacyTip
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.SupervisedUserCircle
import androidx.compose.material.icons.rounded.Vibration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.kevlina.budgetplus.core.common.R
import com.kevlina.budgetplus.core.common.nav.AddDest
import com.kevlina.budgetplus.core.settings.api.ChartMode
import com.kevlina.budgetplus.core.settings.api.icon
import com.kevlina.budgetplus.core.theme.LocalAppColors
import com.kevlina.budgetplus.core.ui.ConfirmDialog
import com.kevlina.budgetplus.core.ui.DropdownItem
import com.kevlina.budgetplus.core.ui.DropdownMenu
import com.kevlina.budgetplus.core.ui.InputDialog
import com.kevlina.budgetplus.core.ui.Switch
import com.kevlina.budgetplus.core.ui.containerPadding
import com.kevlina.budgetplus.feature.settings.member.MembersDialog

@Composable
internal fun SettingsContent(
    navController: NavController,
    vm: SettingsViewModel,
    showMembers: Boolean,
    modifier: Modifier = Modifier,
) {
    val isBookOwner by vm.isBookOwner.collectAsStateWithLifecycle()
    val isPremium by vm.isPremium.collectAsStateWithLifecycle()
    val vibrateOnInput by vm.vibrator.vibrateOnInput.collectAsStateWithLifecycle()
    val chartMode by vm.chartModel.chartMode.collectAsStateWithLifecycle()

    var isRenameUserDialogShown by remember { mutableStateOf(false) }
    var isRenameBookDialogShown by remember { mutableStateOf(false) }
    var isMembersDialogShown by rememberSaveable { mutableStateOf(showMembers) }
    var isDeleteOrLeaveDialogShown by remember { mutableStateOf(false) }
    var isChartModeDropdownShown by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .containerPadding()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {

        // Content section
        SettingsItem(
            text = stringResource(id = R.string.batch_record_title),
            showCrownAnimation = true,
            roundTop = true,
            onClick = {
                vm.trackBatchRecordClicked()
                if (isPremium) {
                    navController.navigate(AddDest.BatchRecord)
                } else {
                    navController.navigate(AddDest.UnlockPremium)
                }
            }
        )

        if (!isPremium) {
            SettingsItem(
                text = stringResource(id = R.string.premium_hide_ads),
                showCrownAnimation = true,
                onClick = { navController.navigate(AddDest.UnlockPremium) }
            )
        }

        SettingsItem(
            text = stringResource(id = R.string.settings_rename_user),
            icon = Icons.Rounded.AccountCircle,
            onClick = { isRenameUserDialogShown = true }
        )

        SettingsItem(
            text = stringResource(id = R.string.settings_rename_book),
            icon = Icons.Rounded.EditNote,
            onClick = { isRenameBookDialogShown = true }
        )

        SettingsItem(
            text = stringResource(id = R.string.settings_edit_book_currency),
            icon = Icons.Rounded.CurrencyExchange,
            onClick = { navController.navigate(AddDest.CurrencyPicker) }
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
                onClick = vm.navigation::openLanguageSettings
            )
        }

        SettingsItem(
            text = stringResource(id = R.string.color_tone_picker_title),
            icon = Icons.Rounded.ColorLens,
            roundTop = !vm.canSelectLanguage,
            onClick = { navController.navigate(AddDest.Colors()) }
        )

        SettingsItem(
            text = stringResource(id = R.string.settings_input_vibration),
            icon = Icons.Rounded.Vibration,
            verticalPadding = 4.dp,
            action = {
                Switch(
                    checked = vibrateOnInput,
                    onCheckedChange = { vm.vibrator.toggleVibrateOnInput() },
                    modifier = Modifier.padding(end = 10.dp)
                )
            },
            onClick = vm.vibrator::toggleVibrateOnInput
        )

        SettingsItem(
            text = stringResource(id = R.string.settings_chart_mode),
            icon = Icons.AutoMirrored.Rounded.ShowChart,
            onClick = { isChartModeDropdownShown = true },
            action = {
                Box {
                    Image(
                        imageVector = chartMode.icon,
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(LocalAppColors.current.dark),
                        modifier = Modifier.padding(end = 16.dp)
                    )

                    DropdownMenu(
                        expanded = isChartModeDropdownShown,
                        onDismissRequest = { isChartModeDropdownShown = false }
                    ) {
                        DropdownItem(
                            name = stringResource(id = R.string.settings_bar_chart),
                            icon = ChartMode.BarChart.icon,
                            onClick = {
                                vm.chartModel.setChartMode(ChartMode.BarChart)
                                isChartModeDropdownShown = false
                            }
                        )

                        DropdownItem(
                            name = stringResource(id = R.string.settings_pie_chart),
                            icon = ChartMode.PieChart.icon,
                            onClick = {
                                vm.chartModel.setChartMode(ChartMode.PieChart)
                                isChartModeDropdownShown = false
                            }
                        )
                    }
                }
            }
        )

        SettingsItem(
            text = stringResource(id = R.string.settings_share_app),
            icon = Icons.Rounded.Share,
            onClick = vm.navigation::share
        )

        SettingsItem(
            text = stringResource(id = R.string.settings_rate_us),
            icon = Icons.Rounded.Star,
            onClick = vm.navigation::rateUs
        )

        SettingsItem(
            text = stringResource(id = R.string.settings_follow_on_instagram),
            iconRes = R.drawable.ic_instagram,
            onClick = vm.navigation::followOnInstagram
        )

        SettingsItem(
            text = stringResource(id = R.string.settings_contact_us),
            icon = Icons.AutoMirrored.Rounded.ForwardToInbox,
            onClick = vm.navigation::contactUs
        )

        SettingsItem(
            text = stringResource(id = R.string.settings_privacy_policy),
            icon = Icons.Rounded.PrivacyTip,
            roundBottom = true,
            onClick = vm.navigation::viewPrivacyPolicy
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
                Icons.AutoMirrored.Rounded.DirectionsRun
            },
            roundTop = true,
            onClick = { isDeleteOrLeaveDialogShown = true }
        )

        SettingsItem(
            text = stringResource(id = R.string.settings_logout),
            icon = Icons.AutoMirrored.Rounded.Logout,
            roundBottom = true,
            onClick = vm.navigation::logout
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
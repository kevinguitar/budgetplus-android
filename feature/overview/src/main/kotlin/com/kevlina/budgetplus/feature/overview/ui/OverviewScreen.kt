package com.kevlina.budgetplus.feature.overview.ui

import android.Manifest
import android.app.Activity
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.FileDownload
import androidx.compose.material.icons.rounded.Segment
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kevlina.budgetplus.core.common.R
import com.kevlina.budgetplus.core.common.hasPermission
import com.kevlina.budgetplus.core.common.nav.Navigator
import com.kevlina.budgetplus.core.common.shortFormatted
import com.kevlina.budgetplus.core.ui.AdaptiveScreen
import com.kevlina.budgetplus.core.ui.ConfirmDialog
import com.kevlina.budgetplus.core.ui.MenuAction
import com.kevlina.budgetplus.core.ui.TopBar
import com.kevlina.budgetplus.core.ui.bubble.BubbleDest
import com.kevlina.budgetplus.feature.overview.OverviewMode
import com.kevlina.budgetplus.feature.overview.OverviewViewModel

@Composable
fun OverviewScreen(navigator: Navigator) {

    val vm = hiltViewModel<OverviewViewModel>()

    val mode by vm.mode.collectAsStateWithLifecycle()
    val bookName by vm.bookName.collectAsStateWithLifecycle()
    val balance by vm.balance.collectAsStateWithLifecycle()
    val isHideAds by vm.isHideAds.collectAsStateWithLifecycle()

    var isExportDialogShown by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val permissionRequester = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            vm.exportToCsv(context)
        } else {
            vm.showWriteFilePermissionHint()
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {

        TopBar(
            title = stringResource(id = R.string.overview_title, bookName.orEmpty()),
            menuActions = {
                MenuAction(
                    imageVector = when (mode) {
                        OverviewMode.AllRecords -> Icons.Rounded.Segment
                        OverviewMode.GroupByCategories -> Icons.Rounded.BarChart
                    },
                    description = stringResource(id = R.string.overview_details_title),
                    onClick = vm::toggleMode,
                    modifier = Modifier.onGloballyPositioned {
                        vm.highlightModeButton(
                            BubbleDest.OverviewMode(
                                size = it.size,
                                offset = it.positionInRoot()
                            )
                        )
                    }
                )

                MenuAction(
                    imageVector = Icons.Rounded.FileDownload,
                    description = stringResource(id = R.string.export_cta),
                    onClick = { isExportDialogShown = true },
                    modifier = Modifier.onGloballyPositioned {
                        vm.highlightExportButton(
                            BubbleDest.OverviewExport(
                                size = it.size,
                                offset = it.positionInRoot()
                            )
                        )
                    }
                )
            }
        )

        AdaptiveScreen(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .weight(1F),
            regularContent = {
                OverviewContent(
                    navigator = navigator,
                    balance = balance,
                    isHideAds = isHideAds,
                )
            },
            wideContent = {
                OverviewContentWide(
                    navigator = navigator,
                    balance = balance,
                    isHideAds = isHideAds,
                )
            }
        )
    }

    val fromDate by vm.timeModel.fromDate.collectAsStateWithLifecycle()
    val untilDate by vm.timeModel.untilDate.collectAsStateWithLifecycle()
    val activity = LocalContext.current as Activity
    val writePermission = Manifest.permission.WRITE_EXTERNAL_STORAGE

    if (isExportDialogShown) {
        ConfirmDialog(
            message = stringResource(
                id = R.string.export_csv_confirmation,
                fromDate.shortFormatted,
                untilDate.shortFormatted
            ),
            onConfirm = {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q && !activity.hasPermission(writePermission)) {
                    permissionRequester.launch(writePermission)
                } else {
                    vm.exportToCsv(context)
                }
                isExportDialogShown = false
            },
            onDismiss = { isExportDialogShown = false }
        )
    }
}

enum class OverviewUiType {
    Header, Record, Group, ZeroCase, Loader
}
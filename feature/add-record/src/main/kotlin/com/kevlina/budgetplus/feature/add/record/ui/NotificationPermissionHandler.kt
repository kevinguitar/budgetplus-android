package com.kevlina.budgetplus.feature.add.record.ui

import android.Manifest
import android.app.Activity
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.kevlina.budgetplus.core.common.consumeEach
import com.kevlina.budgetplus.core.common.hasPermission
import com.kevlina.budgetplus.core.common.shouldShowRationale
import com.kevlina.budgetplus.feature.add.record.RecordViewModel
import kotlinx.coroutines.flow.collect

@Composable
fun NotificationPermissionHandler() {

    val vm = hiltViewModel<RecordViewModel>()
    val activity = LocalContext.current as Activity

    val permissionRequester = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (!isGranted) vm.showNotificationPermissionHint()
    }

    LaunchedEffect(key1 = vm) {
        vm.requestPermissionEvent
            .consumeEach {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return@consumeEach
                val permission = Manifest.permission.POST_NOTIFICATIONS
                if (!activity.hasPermission(permission) && !activity.shouldShowRationale(permission)) {
                    permissionRequester.launch(permission)
                }
            }
            .collect()
    }
}
package com.kevlina.budgetplus.feature.add.record

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.kevlina.budgetplus.core.common.consumeEach
import com.kevlina.budgetplus.core.common.requestNotificationPermission
import com.kevlina.budgetplus.feature.add.record.vm.RecordViewModel
import kotlinx.coroutines.flow.launchIn

@Composable
fun NotificationPermissionHandler() {

    val vm = hiltViewModel<RecordViewModel>()
    val context = LocalContext.current

    val permissionRequester = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (!isGranted) vm.showNotificationPermissionHint()
    }

    LaunchedEffect(key1 = vm) {
        vm.requestPermissionEvent
            .consumeEach { permissionRequester.requestNotificationPermission(context) }
            .launchIn(this)
    }
}
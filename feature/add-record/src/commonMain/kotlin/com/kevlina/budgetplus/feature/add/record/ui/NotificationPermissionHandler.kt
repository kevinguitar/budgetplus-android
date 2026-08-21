package com.kevlina.budgetplus.feature.add.record.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import com.kevlina.budgetplus.core.common.consumeEach
import com.kevlina.budgetplus.feature.add.record.RecordViewModel
import dev.icerock.moko.permissions.DeniedException
import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.compose.BindEffect
import dev.icerock.moko.permissions.compose.rememberPermissionsControllerFactory
import dev.icerock.moko.permissions.notifications.REMOTE_NOTIFICATION
import dev.zacsweers.metrox.viewmodel.metroViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlin.time.Duration.Companion.seconds

@Composable
fun NotificationPermissionHandler() {

    val vm = metroViewModel<RecordViewModel>()
    val factory = rememberPermissionsControllerFactory()
    val controller = remember(factory) { factory.createPermissionsController() }

    BindEffect(controller)

    LaunchedEffect(vm) {
        vm.requestPermissionEvent
            .consumeEach {
                try {
                    // Wait for the done animation to finish before showing the permission dialog.
                    delay(1.seconds)
                    controller.providePermission(Permission.REMOTE_NOTIFICATION)
                } catch (_: DeniedException) {
                    vm.showNotificationPermissionHint()
                } catch (_: Exception) {
                    // Do nothing
                }
            }
            .collect()
    }
}
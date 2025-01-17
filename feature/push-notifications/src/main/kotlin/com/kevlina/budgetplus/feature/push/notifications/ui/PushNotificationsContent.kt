package com.kevlina.budgetplus.feature.push.notifications.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kevlina.budgetplus.core.common.R
import com.kevlina.budgetplus.core.theme.LocalAppColors
import com.kevlina.budgetplus.core.ui.Button
import com.kevlina.budgetplus.core.ui.ConfirmDialog
import com.kevlina.budgetplus.core.ui.FontSize
import com.kevlina.budgetplus.core.ui.Text
import com.kevlina.budgetplus.core.ui.TextField
import com.kevlina.budgetplus.core.ui.containerPadding
import com.kevlina.budgetplus.feature.push.notifications.PushNotificationsViewModel

@Composable
internal fun PushNotificationsContent(
    navigateUp: () -> Unit,
) {

    val vm = hiltViewModel<PushNotificationsViewModel>()

    val sendToCn by vm.sendToCn.collectAsStateWithLifecycle()
    val sendToJa by vm.sendToJa.collectAsStateWithLifecycle()
    val sendToEn by vm.sendToEn.collectAsStateWithLifecycle()
    val navigateToGooglePlay by vm.navigateToGooglePlay.collectAsStateWithLifecycle()

    var isConfirmationDialogShown by remember { mutableStateOf(false) }

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .containerPadding()
            .padding(16.dp)
            .imePadding()
    ) {
        LanguageBlock(
            textRes = R.string.push_notif_language_zh_tw,
            title = vm.titleTw,
            description = vm.descTw,
            isOptional = false
        )

        LanguageBlock(
            textRes = R.string.push_notif_language_zh_cn,
            title = vm.titleCn,
            description = vm.descCn,
            enabled = sendToCn,
            onEnableUpdate = { vm.sendToCn.value = it }
        )

        LanguageBlock(
            textRes = R.string.push_notif_language_ja,
            title = vm.titleJa,
            description = vm.descJa,
            enabled = sendToJa,
            onEnableUpdate = { vm.sendToJa.value = it }
        )

        LanguageBlock(
            textRes = R.string.push_notif_language_en,
            title = vm.titleEn,
            description = vm.descEn,
            enabled = sendToEn,
            onEnableUpdate = { vm.sendToEn.value = it }
        )

        SwitchBlock(
            title = stringResource(id = R.string.push_notif_navigate_to_google_play),
            checked = navigateToGooglePlay,
            onCheckChanged = { vm.navigateToGooglePlay.value = it }
        )

        if (!navigateToGooglePlay) {
            Text(
                text = stringResource(id = R.string.push_notif_deeplink),
                fontSize = FontSize.Large,
                fontWeight = FontWeight.SemiBold
            )

            TextField(
                state = vm.deeplink,
                title = "",
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = vm::sendToInternalTopic,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(id = R.string.push_notif_send_to_internal_topic),
                color = LocalAppColors.current.light,
                fontSize = FontSize.Large,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(vertical = 4.dp, horizontal = 24.dp)
            )
        }

        Button(
            onClick = { isConfirmationDialogShown = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(id = R.string.push_notif_send_to_everyone),
                color = LocalAppColors.current.light,
                fontSize = FontSize.Large,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(vertical = 4.dp, horizontal = 24.dp)
            )
        }
    }

    if (isConfirmationDialogShown) {
        ConfirmDialog(
            message = stringResource(id = R.string.push_notif_send_to_everyone_confirmation),
            onConfirm = {
                vm.sendToEveryone()
                isConfirmationDialogShown = false
                navigateUp()
            },
            onDismiss = { isConfirmationDialogShown = false }
        )
    }
}
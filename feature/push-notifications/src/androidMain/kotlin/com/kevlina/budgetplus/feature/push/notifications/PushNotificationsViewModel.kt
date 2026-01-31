package com.kevlina.budgetplus.feature.push.notifications

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import budgetplus.core.common.generated.resources.Res
import budgetplus.core.common.generated.resources.push_notif_sent_success
import com.kevlina.budgetplus.core.common.SnackbarSender
import com.kevlina.budgetplus.core.common.di.ViewModelKey
import com.kevlina.budgetplus.core.common.di.ViewModelScope
import com.kevlina.budgetplus.core.data.AuthManager
import com.kevlina.budgetplus.core.data.PushDbMediator
import com.kevlina.budgetplus.core.data.local.PreferenceHolder
import com.kevlina.budgetplus.core.data.remote.PushNotificationData
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Named
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.time.Clock

@ViewModelKey(PushNotificationsViewModel::class)
@ContributesIntoMap(ViewModelScope::class)
class PushNotificationsViewModel private constructor(
    private val preferenceHolder: PreferenceHolder,
    translator: Translator,
    @Named("google_play_url") private val googlePlayUrl: String,
    @Named("default_deeplink") private val defaultDeeplink: String,
    private val pushDbMediator: PushDbMediator,
    private val authManager: AuthManager,
    private val snackbarSender: SnackbarSender,
) : ViewModel() {

    private val titleTwFlow = preferenceHolder.bindString("\uD83C\uDF1E炎夏八月，一起編織理財夢！")
    private val descTwFlow = preferenceHolder.bindString(
        "記得目標，存款不停歇！記帳確實，未來更悠遊！將花費化為理財力！GO~"
    )

    private val titleJaFlow = preferenceHolder.bindString("新しい月ですよ！")
    private val descJaFlow = preferenceHolder.bindString(
        "月初めに支出の追跡を始めましょう \uD83D\uDE4C"
    )

    private val titleEnFlow = preferenceHolder.bindString("It's a new month!")
    private val descEnFlow = preferenceHolder.bindString(
        "Track your expenses starting from the beginning of the month \uD83D\uDE4C"
    )

    private val deeplinkFlow = preferenceHolder.bindString("")

    val titleTw = TextFieldState(runBlocking { titleTwFlow.getValue(this, ::titleTwFlow).first() })
    val descTw = TextFieldState(runBlocking { descTwFlow.getValue(this, ::descTwFlow).first() })

    val sendToCn = MutableStateFlow(true)
    val titleCn = TextFieldState()
    val descCn = TextFieldState()

    val sendToJa = MutableStateFlow(true)
    val titleJa = TextFieldState(runBlocking { titleJaFlow.getValue(this, ::titleJaFlow).first() })
    val descJa = TextFieldState(runBlocking { descJaFlow.getValue(this, ::descJaFlow).first() })

    val sendToEn = MutableStateFlow(true)
    val titleEn = TextFieldState(runBlocking { titleEnFlow.getValue(this, ::titleEnFlow).first() })
    val descEn = TextFieldState(runBlocking { descEnFlow.getValue(this, ::descEnFlow).first() })

    val navigateToGooglePlay = MutableStateFlow(false)
    val deeplink = TextFieldState(runBlocking { deeplinkFlow.getValue(this, ::deeplinkFlow).first() })

    init {
        snapshotFlow { titleTw.text }
            .debounce(INPUT_DEBOUNCE_MS)
            .mapLatest {
                val title = translator.translate(
                    text = it.toString(),
                    sourceLanCode = LAN_CODE_TW,
                    targetLanCode = LAN_CODE_CN
                )
                titleCn.setTextAndPlaceCursorAtEnd(title)
            }
            .launchIn(viewModelScope)

        snapshotFlow { descTw.text }
            .debounce(INPUT_DEBOUNCE_MS)
            .mapLatest {
                val desc = translator.translate(
                    text = it.toString(),
                    sourceLanCode = LAN_CODE_TW,
                    targetLanCode = LAN_CODE_CN
                )
                descCn.setTextAndPlaceCursorAtEnd(desc)
            }
            .launchIn(viewModelScope)
    }

    fun sendToInternalTopic() {
        recordToPushDb(isInternal = true)
    }

    fun sendToEveryone() {
        recordToPushDb(isInternal = false)
    }

    private fun recordToPushDb(isInternal: Boolean) {
        saveToCache()
        viewModelScope.launch {
            try {
                val user = authManager.userState.first()
                if (user?.internal != true) {
                    error("An external user is trying to send notification!!!")
                }
                pushDbMediator.recordPushNotification(PushNotificationData(
                    internal = isInternal,
                    titleTw = titleTw.text.trim(),
                    descTw = descTw.text.trim(),
                    titleCn = titleCn.text.trim().takeIf { sendToCn.value },
                    descCn = descCn.text.trim().takeIf { sendToCn.value },
                    titleJa = titleJa.text.trim().takeIf { sendToJa.value },
                    descJa = descJa.text.trim().takeIf { sendToJa.value },
                    titleEn = titleEn.text.trim().takeIf { sendToEn.value },
                    descEn = descEn.text.trim().takeIf { sendToEn.value },
                    deeplink = when {
                        navigateToGooglePlay.value -> googlePlayUrl
                        deeplink.text.isNotBlank() -> deeplink.text.trim()
                        else -> defaultDeeplink
                    },
                    sentOn = Clock.System.now().toEpochMilliseconds()
                ))
                snackbarSender.send(Res.string.push_notif_sent_success)
            } catch (e: Exception) {
                snackbarSender.sendError(e)
            }
        }
    }

    private fun saveToCache() {
        viewModelScope.launch {
            preferenceHolder.setString("titleTwCache", titleTw.text.toString())
            preferenceHolder.setString("descTwCache", descTw.text.toString())
            preferenceHolder.setString("titleJaCache", titleJa.text.toString())
            preferenceHolder.setString("descJaCache", descJa.text.toString())
            preferenceHolder.setString("titleEnCache", titleEn.text.toString())
            preferenceHolder.setString("descEnCache", descEn.text.toString())
            preferenceHolder.setString("deeplinkCache", deeplink.text.toString())
        }
    }

    private companion object {
        const val INPUT_DEBOUNCE_MS = 200L
        const val LAN_CODE_TW = "zh-TW"
        const val LAN_CODE_CN = "zh-CN"
    }
}
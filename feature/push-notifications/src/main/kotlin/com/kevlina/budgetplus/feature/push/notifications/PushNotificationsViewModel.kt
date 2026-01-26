package com.kevlina.budgetplus.feature.push.notifications

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kevlina.budgetplus.core.common.R
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
import kotlin.time.Clock

@ViewModelKey(PushNotificationsViewModel::class)
@ContributesIntoMap(ViewModelScope::class)
class PushNotificationsViewModel private constructor(
    preferenceHolder: PreferenceHolder,
    translator: Translator,
    @Named("google_play_url") private val googlePlayUrl: String,
    @Named("default_deeplink") private val defaultDeeplink: String,
    private val pushDbMediator: PushDbMediator,
    private val authManager: AuthManager,
    private val snackbarSender: SnackbarSender,
) : ViewModel() {

    private var titleTwCache by preferenceHolder.bindString("\uD83C\uDF1E炎夏八月，一起編織理財夢！")
    private var descTwCache by preferenceHolder.bindString(
        "記得目標，存款不停歇！記帳確實，未來更悠遊！將花費化為理財力！GO~"
    )

    private var titleJaCache by preferenceHolder.bindString("新しい月ですよ！")
    private var descJaCache by preferenceHolder.bindString(
        "月初めに支出の追跡を始めましょう \uD83D\uDE4C"
    )

    private var titleEnCache by preferenceHolder.bindString("It's a new month!")
    private var descEnCache by preferenceHolder.bindString(
        "Track your expenses starting from the beginning of the month \uD83D\uDE4C"
    )

    private var deeplinkCache by preferenceHolder.bindString("")

    val titleTw = TextFieldState(titleTwCache)
    val descTw = TextFieldState(descTwCache)

    val sendToCn = MutableStateFlow(true)
    val titleCn = TextFieldState()
    val descCn = TextFieldState()

    val sendToJa = MutableStateFlow(true)
    val titleJa = TextFieldState(titleJaCache)
    val descJa = TextFieldState(descJaCache)

    val sendToEn = MutableStateFlow(true)
    val titleEn = TextFieldState(titleEnCache)
    val descEn = TextFieldState(descEnCache)

    val navigateToGooglePlay = MutableStateFlow(false)
    val deeplink = TextFieldState(deeplinkCache)

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
                snackbarSender.send(R.string.push_notif_sent_success)
            } catch (e: Exception) {
                snackbarSender.sendError(e)
            }
        }
    }

    private fun saveToCache() {
        titleTwCache = titleTw.text.toString()
        descTwCache = descTw.text.toString()

        titleJaCache = titleJa.text.toString()
        descJaCache = descJa.text.toString()

        titleEnCache = titleEn.text.toString()
        descEnCache = descEn.text.toString()

        deeplinkCache = deeplink.text.toString()
    }

    private companion object {
        const val INPUT_DEBOUNCE_MS = 200L
        const val LAN_CODE_TW = "zh-TW"
        const val LAN_CODE_CN = "zh-CN"
    }
}
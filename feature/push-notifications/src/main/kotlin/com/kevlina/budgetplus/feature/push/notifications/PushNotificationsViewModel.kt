package com.kevlina.budgetplus.feature.push.notifications

import androidx.compose.foundation.text.input.TextFieldState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kevlina.budgetplus.core.common.R
import com.kevlina.budgetplus.core.common.SnackbarSender
import com.kevlina.budgetplus.core.data.AuthManager
import com.kevlina.budgetplus.core.data.PushDbMediator
import com.kevlina.budgetplus.core.data.local.PreferenceHolder
import com.kevlina.budgetplus.core.data.remote.PushNotificationData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
internal class PushNotificationsViewModel @Inject constructor(
    preferenceHolder: PreferenceHolder,
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

    private var titleCnCache by preferenceHolder.bindString("\uD83C\uDF1E炎夏八月，一起编织理财梦！")
    private var descCnCache by preferenceHolder.bindString(
        "记得目标，存款不停歇！记帐确实，未来更悠游！将花费化为理财力！GO~"
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
    val titleCn = TextFieldState(titleCnCache)
    val descCn = TextFieldState(descCnCache)

    val sendToJa = MutableStateFlow(true)
    val titleJa = TextFieldState(titleJaCache)
    val descJa = TextFieldState(descJaCache)

    val sendToEn = MutableStateFlow(true)
    val titleEn = TextFieldState(titleEnCache)
    val descEn = TextFieldState(descEnCache)

    val navigateToGooglePlay = MutableStateFlow(false)
    val deeplink = TextFieldState(deeplinkCache)

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
                    sentOn = System.currentTimeMillis()
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

        titleCnCache = titleCn.text.toString()
        descCnCache = descCn.text.toString()

        titleJaCache = titleJa.text.toString()
        descJaCache = descJa.text.toString()

        titleEnCache = titleEn.text.toString()
        descEnCache = descEn.text.toString()

        deeplinkCache = deeplink.text.toString()
    }
}
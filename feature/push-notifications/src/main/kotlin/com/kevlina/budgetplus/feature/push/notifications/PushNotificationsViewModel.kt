package com.kevlina.budgetplus.feature.push.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kevlina.budgetplus.core.common.R
import com.kevlina.budgetplus.core.common.Toaster
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
    private val toaster: Toaster,
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

    val titleTw = MutableStateFlow(titleTwCache)
    val descTw = MutableStateFlow(descTwCache)

    val titleCn = MutableStateFlow(titleCnCache)
    val descCn = MutableStateFlow(descCnCache)

    val titleJa = MutableStateFlow(titleJaCache)
    val descJa = MutableStateFlow(descJaCache)

    val titleEn = MutableStateFlow(titleEnCache)
    val descEn = MutableStateFlow(descEnCache)

    val navigateToGooglePlay = MutableStateFlow(false)
    val deeplink = MutableStateFlow(deeplinkCache)

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
                    error("!!!An external user is trying to send notification!!!")
                }
                pushDbMediator.recordPushNotification(PushNotificationData(
                    internal = isInternal,
                    titleTw = titleTw.value,
                    descTw = descTw.value,
                    titleCn = titleCn.value,
                    descCn = descCn.value,
                    titleJa = titleJa.value,
                    descJa = descJa.value,
                    titleEn = titleEn.value,
                    descEn = descEn.value,
                    deeplink = when {
                        navigateToGooglePlay.value -> googlePlayUrl
                        deeplink.value.isNotBlank() -> deeplink.value
                        else -> defaultDeeplink
                    }
                ))
                toaster.showMessage(R.string.push_notif_sent_success)
            } catch (e: Exception) {
                toaster.showError(e)
            }
        }
    }

    private fun saveToCache() {
        titleTwCache = titleTw.value
        descTwCache = descTw.value

        titleCnCache = titleCn.value
        descCnCache = descCn.value

        titleJaCache = titleJa.value
        descJaCache = descJa.value

        titleEnCache = titleEn.value
        descEnCache = descEn.value

        deeplinkCache = deeplink.value
    }
}
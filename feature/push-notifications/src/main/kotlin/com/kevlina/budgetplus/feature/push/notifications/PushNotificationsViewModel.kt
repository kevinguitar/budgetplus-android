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
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
internal class PushNotificationsViewModel @Inject constructor(
    preferenceHolder: PreferenceHolder,
    translator: Translator,
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

    private var titleEnCache by preferenceHolder.bindString("It's a new month!")
    private var descEnCache by preferenceHolder.bindString(
        "Track your expenses starting from the beginning of the month \uD83D\uDE4C"
    )

    val titleTw = MutableStateFlow(titleTwCache)
    val descTw = MutableStateFlow(descTwCache)

    val titleCn = titleTw
        .debounce(INPUT_DEBOUNCE_MS)
        .mapLatest { translator.translate(text = it, sourceLanCode = LAN_CODE_TW, targetLanCode = LAN_CODE_CN) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

    val descCn = descTw
        .debounce(INPUT_DEBOUNCE_MS)
        .mapLatest { translator.translate(text = it, sourceLanCode = LAN_CODE_TW, targetLanCode = LAN_CODE_CN) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

    val titleEn = MutableStateFlow(titleEnCache)
    val descEn = MutableStateFlow(descEnCache)

    val navigateToGooglePlay = MutableStateFlow(false)
    val deeplink = MutableStateFlow("")

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

        titleEnCache = titleEn.value
        descEnCache = descEn.value
    }

    private companion object {
        const val INPUT_DEBOUNCE_MS = 200L
        const val LAN_CODE_TW = "zh-TW"
        const val LAN_CODE_CN = "zh-CN"
    }
}
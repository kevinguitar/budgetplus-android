package com.kevlina.budgetplus.feature.settings

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kevlina.budgetplus.core.common.R
import com.kevlina.budgetplus.core.common.Toaster
import com.kevlina.budgetplus.core.common.Tracker
import com.kevlina.budgetplus.core.common.combineState
import com.kevlina.budgetplus.core.common.mapState
import com.kevlina.budgetplus.core.common.nav.ARG_ENABLE_ONE_TAP
import com.kevlina.budgetplus.core.common.nav.NavigationFlow
import com.kevlina.budgetplus.core.common.nav.NavigationInfo
import com.kevlina.budgetplus.core.common.sendEvent
import com.kevlina.budgetplus.core.data.AuthManager
import com.kevlina.budgetplus.core.data.BookRepo
import com.kevlina.budgetplus.core.data.VibratorManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named
import kotlin.reflect.KClass

@HiltViewModel
internal class SettingsViewModel @Inject constructor(
    private val bookRepo: BookRepo,
    private val authManager: AuthManager,
    private val toaster: Toaster,
    private val tracker: Tracker,
    val vibrator: VibratorManager,
    @ApplicationContext private val context: Context,
    @Named("app_package") private val appPackage: String,
    @Named("google_play_url") private val googlePlayUrl: String,
    @Named("auth") private val authDest: KClass<out Activity>,
) : ViewModel() {

    val navigation = NavigationFlow()

    val bookName = bookRepo.bookState.mapState { it?.name }
    val isBookOwner = bookRepo.bookState.combineState(
        other = authManager.userState,
        scope = viewModelScope
    ) { book, user ->
        book != null && book.ownerId == user?.id
    }

    val isPremium = authManager.isPremium
    val canSelectLanguage: Boolean
        get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU

    val currentUsername get() = authManager.userState.value?.name
    val currentBookName get() = bookRepo.bookState.value?.name

    fun trackBatchRecordClicked() {
        tracker.logEvent("settings_batch_record_click")
    }

    fun renameUser(newName: String) {
        viewModelScope.launch {
            try {
                authManager.renameUser(newName)
                tracker.logEvent("user_renamed")
            } catch (e: Exception) {
                toaster.showError(e)
            }
        }
    }

    fun renameBook(newName: String) {
        viewModelScope.launch {
            try {
                bookRepo.renameBook(newName)
            } catch (e: Exception) {
                toaster.showError(e)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun openLanguageSettings(context: Context) {
        val intent = Intent(Settings.ACTION_APP_LOCALE_SETTINGS).apply {
            data = "package:$appPackage".toUri()
        }
        context.startActivity(intent)
        tracker.logEvent("settings_language_click")
    }

    fun share(context: Context) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, context.getString(R.string.settings_share_app_message, googlePlayUrl))
        }
        context.startActivity(Intent.createChooser(intent, context.getString(R.string.settings_share_app)))
        tracker.logEvent("settings_share_app_click")
    }

    fun rateUs(context: Context) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = googlePlayUrl.toUri()
        }
        context.startActivity(intent)
        tracker.logEvent("settings_rate_us_click")
    }

    fun contactUs(context: Context) {
        context as Activity
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = "mailto:".toUri()
            putExtra(Intent.EXTRA_EMAIL, arrayOf("budgetplussg@gmail.com"))
            putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.settings_contact_us))
        }
        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
            tracker.logEvent("settings_contact_us_click")
        } else {
            toaster.showMessage(R.string.settings_no_email_app_found)
        }
    }

    fun deleteOrLeave() {
        viewModelScope.launch {
            val isBookOwner = isBookOwner.value
            val bookName = bookName.value
            try {
                bookRepo.leaveOrDeleteBook()
                toaster.showMessage(context.getString(if (isBookOwner) {
                    R.string.settings_book_deleted
                } else {
                    R.string.settings_book_left
                }, bookName))
            } catch (e: Exception) {
                toaster.showError(e)
            }
        }
    }

    fun logout() {
        authManager.logout()
        tracker.logEvent("logout")

        val navInfo = NavigationInfo(
            destination = authDest,
            bundle = Bundle().apply { putBoolean(ARG_ENABLE_ONE_TAP, false) }
        )
        navigation.sendEvent(navInfo)
    }
}
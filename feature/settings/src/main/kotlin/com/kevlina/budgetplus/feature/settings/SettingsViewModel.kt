package com.kevlina.budgetplus.feature.settings

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kevlina.budgetplus.core.common.R
import com.kevlina.budgetplus.core.common.StringProvider
import com.kevlina.budgetplus.core.common.Toaster
import com.kevlina.budgetplus.core.common.Tracker
import com.kevlina.budgetplus.core.common.combineState
import com.kevlina.budgetplus.core.common.mapState
import com.kevlina.budgetplus.core.common.nav.NavigationAction
import com.kevlina.budgetplus.core.common.nav.NavigationFlow
import com.kevlina.budgetplus.core.common.sendEvent
import com.kevlina.budgetplus.core.data.AuthManager
import com.kevlina.budgetplus.core.data.BookRepo
import com.kevlina.budgetplus.core.data.VibratorManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
internal class SettingsViewModel @Inject constructor(
    private val bookRepo: BookRepo,
    private val authManager: AuthManager,
    private val stringProvider: StringProvider,
    private val toaster: Toaster,
    private val tracker: Tracker,
    val vibrator: VibratorManager,
    val navigation: NavigationFlow,
    @Named("app_package") private val appPackage: String,
    @Named("google_play_url") private val googlePlayUrl: String,
    @Named("instagram_url") private val instagramUrl: String,
    @Named("privacy_policy_url") private val privacyPolicyUrl: String,
    @Named("contact_email") private val contactEmail: String,
    @Named("logout") private val logoutNavigationAction: NavigationAction,
) : ViewModel() {

    val bookName = bookRepo.bookState.mapState { it?.name }
    val isBookOwner = bookRepo.bookState.combineState(
        other = authManager.userState,
        scope = viewModelScope
    ) { book, user ->
        book != null && book.ownerId == user?.id
    }

    val isPremium = authManager.isPremium

    @get:SuppressLint("AnnotateVersionCheck")
    val canSelectLanguage: Boolean
        get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU

    val isInsider = authManager.userState.mapState {
        it?.internal == true
    }

    val currentUsername get() = authManager.userState.value?.name
    val currentBookName get() = bookRepo.bookState.value?.name

    fun trackBatchRecordClicked() {
        tracker.logEvent("settings_batch_record_click")
    }

    fun renameUser(newName: String) {
        viewModelScope.launch {
            try {
                authManager.renameUser(newName)
                toaster.showMessage(stringProvider[R.string.settings_rename_user_success, newName])
            } catch (e: Exception) {
                toaster.showError(e)
            }
        }
    }

    fun renameBook(newName: String) {
        viewModelScope.launch {
            try {
                bookRepo.renameBook(newName)
                toaster.showMessage(stringProvider[R.string.settings_rename_book_success, newName])
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
            putExtra(Intent.EXTRA_TEXT, stringProvider[R.string.settings_share_app_message, googlePlayUrl])
        }
        context.startActivity(Intent.createChooser(intent, stringProvider[R.string.settings_share_app]))
        tracker.logEvent("settings_share_app_click")
    }

    fun rateUs(context: Context) {
        context.visitUrl(googlePlayUrl)
        tracker.logEvent("settings_rate_us_click")
    }

    fun followOnInstagram(context: Context) {
        context.visitUrl(instagramUrl)
        tracker.logEvent("settings_follow_instagram_click")
    }

    fun contactUs(context: Context) {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = "mailto:".toUri()
            putExtra(Intent.EXTRA_EMAIL, arrayOf(contactEmail))
            putExtra(Intent.EXTRA_SUBJECT, stringProvider[R.string.settings_contact_us])
            putExtra(Intent.EXTRA_TEXT, "User id: ${authManager.requireUserId()}\n")
        }
        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
            tracker.logEvent("settings_contact_us_click")
        } else {
            toaster.showMessage(R.string.settings_no_email_app_found)
        }
    }

    fun viewPrivacyPolicy(context: Context) {
        context.visitUrl(privacyPolicyUrl)
        tracker.logEvent("settings_privacy_policy_click")
    }

    fun deleteOrLeave() {
        viewModelScope.launch {
            val isBookOwner = isBookOwner.value
            val bookName = bookName.value
            try {
                bookRepo.leaveOrDeleteBook()
                toaster.showMessage(stringProvider[if (isBookOwner) {
                    R.string.settings_book_deleted
                } else {
                    R.string.settings_book_left
                }, bookName.orEmpty()])
            } catch (e: Exception) {
                toaster.showError(e)
            }
        }
    }

    fun logout() {
        authManager.logout()
        navigation.sendEvent(logoutNavigationAction)
    }

    private fun Context.visitUrl(url: String) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = url.toUri()
        }
        startActivity(intent)
    }
}
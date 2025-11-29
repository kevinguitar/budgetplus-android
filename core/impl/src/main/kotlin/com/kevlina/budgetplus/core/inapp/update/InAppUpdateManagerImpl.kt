package com.kevlina.budgetplus.core.inapp.update

import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.ktx.AppUpdateResult
import com.google.android.play.core.ktx.isFlexibleUpdateAllowed
import com.google.android.play.core.ktx.isImmediateUpdateAllowed
import com.google.android.play.core.ktx.requestUpdateFlow
import com.kevlina.budgetplus.core.common.Tracker
import com.kevlina.budgetplus.core.data.local.PreferenceHolder
import com.kevlina.budgetplus.core.inapp.update.InAppUpdateManagerImpl.Companion.DAYS_FOR_FLEXIBLE_UPDATE
import com.kevlina.budgetplus.core.inapp.update.InAppUpdateManagerImpl.Companion.DAYS_FOR_IMMEDIATE_UPDATE
import com.kevlina.budgetplus.inapp.update.InAppUpdateManager
import com.kevlina.budgetplus.inapp.update.InAppUpdateState
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber

@ContributesBinding(AppScope::class)
class InAppUpdateManagerImpl(
    private val activity: ComponentActivity,
    private val tracker: Tracker,
    preferenceHolder: PreferenceHolder,
) : InAppUpdateManager {

    private val _updateState = MutableStateFlow<InAppUpdateState>(InAppUpdateState.NotStarted)
    override val updateState: StateFlow<InAppUpdateState> = _updateState.asStateFlow()

    private val appUpdateManager = AppUpdateManagerFactory.create(activity)
    private val scope = activity.lifecycleScope

    // The version code when we requested the update last time.
    private var lastRequestedVersionCode by preferenceHolder.bindInt(0)

    private val AppUpdateInfo.shouldRequestImmediateUpdate: Boolean
        get() = updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
            && isImmediateUpdateAllowed
            && (clientVersionStalenessDays() ?: 0) >= DAYS_FOR_IMMEDIATE_UPDATE
            && availableVersionCode() > lastRequestedVersionCode

    private val AppUpdateInfo.shouldRequestFlexibleUpdate: Boolean
        get() = updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
            && isFlexibleUpdateAllowed
            && (clientVersionStalenessDays() ?: 0) >= DAYS_FOR_FLEXIBLE_UPDATE
            && availableVersionCode() > lastRequestedVersionCode

    init {
        appUpdateManager.requestUpdateFlow()
            .onEach(::processResult)
            .catch { e -> Timber.e(e) }
            .launchIn(scope)
    }

    private fun processResult(result: AppUpdateResult) {
        when (result) {
            is AppUpdateResult.Available -> {
                val updateInfo = result.updateInfo
                when {
                    updateInfo.shouldRequestImmediateUpdate -> {
                        lastRequestedVersionCode = updateInfo.availableVersionCode()
                        result.startImmediateUpdate(activity, REQ_APP_UPDATE)
                        tracker.logEvent("inapp_update_immediate")
                    }

                    updateInfo.shouldRequestFlexibleUpdate -> {
                        lastRequestedVersionCode = updateInfo.availableVersionCode()
                        result.startFlexibleUpdate(activity, REQ_APP_UPDATE)
                        tracker.logEvent("inapp_update_flexible")
                    }
                }
            }

            is AppUpdateResult.InProgress -> {
                _updateState.value = InAppUpdateState.Downloading
            }

            is AppUpdateResult.Downloaded -> {
                _updateState.value = InAppUpdateState.Downloaded {
                    completeUpdate(result)
                }
            }

            AppUpdateResult.NotAvailable -> {
                _updateState.value = InAppUpdateState.NotAvailable
            }
        }
    }

    private fun completeUpdate(result: AppUpdateResult.Downloaded) {
        scope.launch {
            try {
                result.completeUpdate()
                tracker.logEvent("inapp_update_flexible_complete")
            } catch (e: Exception) {
                Timber.e(e, "Fail to complete flexible update")
            }
        }
    }

    /**
     *  If the latest version is already available on Google Play for [DAYS_FOR_FLEXIBLE_UPDATE]
     *  days, then request a flexible update. If it's already available more than
     *  [DAYS_FOR_IMMEDIATE_UPDATE] days, then request a immediate update.
     */
    private companion object {
        const val DAYS_FOR_FLEXIBLE_UPDATE = 3
        const val DAYS_FOR_IMMEDIATE_UPDATE = 30
        const val REQ_APP_UPDATE = 4820
    }
}
package com.kevlina.budgetplus.book

import com.kevlina.budgetplus.core.common.UiTestFlags
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.firestore

object UiTestEnvironment {

    private var enabled: Boolean = false

    fun configure(emulatorHost: String) {
        if (enabled) return
        Firebase.auth.useEmulator(emulatorHost, 9099)
        Firebase.firestore.useEmulator(emulatorHost, 8080)
        UiTestFlags.enabled = true
        UiTestFlags.persistentSnackbar = true
        enabled = true
    }
}

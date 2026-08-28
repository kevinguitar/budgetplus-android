package com.kevlina.budgetplus.book

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.firestore

object UiTestEnvironment {
    var enabled: Boolean = false
        private set

    fun configure(emulatorHost: String) {
        if (enabled) return
        Firebase.auth.useEmulator(emulatorHost, 9099)
        Firebase.firestore.useEmulator(emulatorHost, 8080)
        enabled = true
    }
}

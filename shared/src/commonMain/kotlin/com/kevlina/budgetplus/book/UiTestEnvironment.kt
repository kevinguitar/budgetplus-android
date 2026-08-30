package com.kevlina.budgetplus.book

import com.kevlina.budgetplus.core.common.UiTestFlags
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.firestore

object UiTestEnvironment {

    fun configure(emulatorHost: String) {
        Firebase.auth.useEmulator(emulatorHost, 9099)
        Firebase.firestore.useEmulator(emulatorHost, 8080)
        UiTestFlags.enabled = true
    }
}

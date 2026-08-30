package com.kevlina.budgetplus.core.common

/**
 * Lightweight, dependency-free flags that the UI-test build flips on at startup so that
 * lower-level modules (e.g. core:ui, core:billing) can adapt their behavior for automated
 * UI testing without depending on the higher-level test environment setup.
 *
 * These are set from UiTestEnvironment.configure() (which also routes Firebase to the
 * emulators), so they are only ever true in the uiTest build / UI_TEST compilation.
 */
object UiTestFlags {

    /** True while running under the automated UI-test environment. */
    var enabled: Boolean = false

    /**
     * When true, transient UI such as snackbars stays visible indefinitely so that
     * Maestro can reliably assert on it.
     */
    var persistentSnackbar: Boolean = false
}

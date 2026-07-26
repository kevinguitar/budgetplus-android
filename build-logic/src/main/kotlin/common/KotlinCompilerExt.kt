package common

import org.jetbrains.kotlin.gradle.dsl.KotlinCommonCompilerOptions
import org.jetbrains.kotlin.gradle.dsl.KotlinCommonCompilerToolOptions

internal fun KotlinCommonCompilerOptions.applyCommonCompilerOptions() {
    optIn.addAll(
        "kotlin.contracts.ExperimentalContracts",
        "kotlinx.coroutines.ExperimentalCoroutinesApi",
        "kotlinx.coroutines.ExperimentalForInheritanceCoroutinesApi",
        "kotlinx.coroutines.FlowPreview"
    )
}

internal fun KotlinCommonCompilerToolOptions.suppressStaleEmbeddedKotlinCompilerWarnings() {
    // Workaround for old kotlin release embedded in the AS
    // See https://youtrack.jetbrains.com/issue/KT-83265/How-to-disable-Explicit-Backing-Fields-compiler-warning#focus=Comments-27-13138960.0-0
    if (isInIdeaSync) {
        freeCompilerArgs.addAll(
            "-XXLanguage:+ExplicitBackingFields",
            "-XXLanguage:+ContextParameters",
            "-XXLanguage:+PropertyParamAnnotationDefaultTargetMode",
        )
    }
}

private val isInIdeaSync
    get() = System.getProperty("idea.sync.active").toBoolean()
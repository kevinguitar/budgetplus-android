package com.kevlina.budgetplus.ads

import com.kevlina.budgetplus.ads.MetaAdvertiserTracking.configurator


/**
 * Bridge for configuring Meta Audience Network's advertiser tracking flag from Kotlin.
 *
 * [FBAudienceNetwork] is not exposed to the KMP cinterop, so the Swift layer registers
 * [configurator] during app startup. Kotlin then invokes it once the real ATT
 * authorization result is known, right before AdMob is initialized.
 *
 * Meta requires that the advertiser tracking flag reflects the actual ATT authorization
 * state. Setting it prematurely (e.g. hardcoded `true` before the user responds to the
 * ATT prompt) causes Meta to drop bidding requests.
 */
object MetaAdvertiserTracking {

    /** Set by the Swift layer. Receives `true` only when ATT was authorized. */
    var configurator: ((enabled: Boolean) -> Unit)? = null

    fun setAdvertiserTrackingEnabled(enabled: Boolean) {
        configurator?.invoke(enabled)
    }
}
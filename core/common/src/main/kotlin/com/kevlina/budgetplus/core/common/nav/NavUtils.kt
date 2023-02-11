package com.kevlina.budgetplus.core.common.nav

/**
 *  Compose navigation is based on a url-like pattern, so we need to avoid using particular symbols
 *  in the path, otherwise it will either crash or cannot navigate at the runtime.
 */
private const val SLASH_REPLACEMENT = "~!@$^*()"
private const val HASH_REPLACEMENT = "$~^*!@()"
private const val MOD_REPLACEMENT = "$^*!()~@"

val String.navKey: String
    get() = replace("/", SLASH_REPLACEMENT)
        .replace("#", HASH_REPLACEMENT)
        .replace("%", MOD_REPLACEMENT)

val String.originalNavValue: String
    get() = replace(SLASH_REPLACEMENT, "/")
        .replace(HASH_REPLACEMENT, "#")
        .replace(MOD_REPLACEMENT, "%")

const val APP_DEEPLINK = "https://budgetplus.cchi.tw"
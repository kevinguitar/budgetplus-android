package com.kevlina.budgetplus.core.billing

/**
 *  Enum version of Google Play Billing statuses.
 *
 *  https://developer.android.com/reference/com/android/billingclient/api/BillingClient.BillingResponseCode
 */
enum class BillingStatus {
    UNKNOWN,

    /** Requested feature is not supported by Play Store on the current device. */
    FEATURE_NOT_SUPPORTED,

    /**
     * Play Store service is not connected now - potentially transient state.
     *
     * E.g. Play Store could have been updated in the background while your app was still
     * running. So feel free to introduce your retry policy for such use case. It should lead to a
     * call to [.startConnection] right after or in some time after you received this code.
     */
    SERVICE_DISCONNECTED,

    /** Success. */
    OK,

    /** User pressed back or canceled a dialog. */
    USER_CANCELED,

    /** Network connection is down. */
    SERVICE_UNAVAILABLE,

    /** Billing API version is not supported for the type requested. */
    BILLING_UNAVAILABLE,

    /** Requested product is not available for purchase. */
    ITEM_UNAVAILABLE,

    /**
     * Invalid arguments provided to the API. This error can also indicate that the application was
     * not correctly signed or properly set up for In-app Billing in Google Play, or does not have
     * the necessary permissions in its manifest.
     */
    DEVELOPER_ERROR,

    /** Fatal error during the API action. */
    ERROR,

    /** Failure to purchase since item is already owned. */
    ITEM_ALREADY_OWNED,

    /** Failure to consume since item is not owned. */
    ITEM_NOT_OWNED
}
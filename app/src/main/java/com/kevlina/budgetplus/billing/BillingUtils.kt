package com.kevlina.budgetplus.billing

import com.android.billingclient.api.BillingClient.BillingResponseCode
import com.kevlina.budgetplus.billing.BillingStatus.*

@Suppress("FunctionName")
internal fun BillingStatus(code: Int) = codes.getValue(code)

private val codes = mapOf(
    null to UNKNOWN,
    BillingResponseCode.SERVICE_TIMEOUT to SERVICE_TIMEOUT,
    BillingResponseCode.FEATURE_NOT_SUPPORTED to FEATURE_NOT_SUPPORTED,
    BillingResponseCode.SERVICE_DISCONNECTED to SERVICE_DISCONNECTED,
    BillingResponseCode.OK to OK,
    BillingResponseCode.USER_CANCELED to USER_CANCELED,
    BillingResponseCode.SERVICE_UNAVAILABLE to SERVICE_UNAVAILABLE,
    BillingResponseCode.BILLING_UNAVAILABLE to BILLING_UNAVAILABLE,
    BillingResponseCode.ITEM_UNAVAILABLE to ITEM_UNAVAILABLE,
    BillingResponseCode.DEVELOPER_ERROR to DEVELOPER_ERROR,
    BillingResponseCode.ERROR to ERROR,
    BillingResponseCode.ITEM_ALREADY_OWNED to ITEM_ALREADY_OWNED,
    BillingResponseCode.ITEM_NOT_OWNED to ITEM_NOT_OWNED
).withDefault { UNKNOWN }
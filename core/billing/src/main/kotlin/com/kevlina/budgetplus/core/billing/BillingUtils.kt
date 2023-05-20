package com.kevlina.budgetplus.core.billing

import com.android.billingclient.api.BillingClient.BillingResponseCode
import com.kevlina.budgetplus.core.billing.BillingStatus.BILLING_UNAVAILABLE
import com.kevlina.budgetplus.core.billing.BillingStatus.DEVELOPER_ERROR
import com.kevlina.budgetplus.core.billing.BillingStatus.ERROR
import com.kevlina.budgetplus.core.billing.BillingStatus.FEATURE_NOT_SUPPORTED
import com.kevlina.budgetplus.core.billing.BillingStatus.ITEM_ALREADY_OWNED
import com.kevlina.budgetplus.core.billing.BillingStatus.ITEM_NOT_OWNED
import com.kevlina.budgetplus.core.billing.BillingStatus.ITEM_UNAVAILABLE
import com.kevlina.budgetplus.core.billing.BillingStatus.OK
import com.kevlina.budgetplus.core.billing.BillingStatus.SERVICE_DISCONNECTED
import com.kevlina.budgetplus.core.billing.BillingStatus.SERVICE_UNAVAILABLE
import com.kevlina.budgetplus.core.billing.BillingStatus.UNKNOWN
import com.kevlina.budgetplus.core.billing.BillingStatus.USER_CANCELED

internal fun BillingStatus(code: Int) = codes.getValue(code)

private val codes = mapOf(
    null to UNKNOWN,
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
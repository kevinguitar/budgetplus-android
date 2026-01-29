package com.kevlina.budgetplus.core.billing

internal class BillingException(override val message: String) : RuntimeException(message)

internal class BillingRestoredException(override val message: String) : RuntimeException(message)
package com.kevlina.budgetplus.core.billing

internal class BillingException : RuntimeException("Billing error")

internal class BillingRestoredException : RuntimeException("Billing restoration error")
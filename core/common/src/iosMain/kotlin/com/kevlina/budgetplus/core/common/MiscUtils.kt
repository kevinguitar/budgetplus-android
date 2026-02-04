package com.kevlina.budgetplus.core.common

import platform.Foundation.NSUUID

actual fun randomUUID(): String = NSUUID().UUIDString()

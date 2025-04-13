package com.kevlina.budgetplus.core.common

import androidx.activity.ComponentActivity

class FakeActivityProvider(
    override val currentActivity: ComponentActivity,
) : ActivityProvider
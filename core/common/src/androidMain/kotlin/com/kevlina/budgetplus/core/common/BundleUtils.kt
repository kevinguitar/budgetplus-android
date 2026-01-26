package com.kevlina.budgetplus.core.common

import android.os.Bundle

inline fun bundle(init: Bundle.() -> Unit) = Bundle().apply(init)

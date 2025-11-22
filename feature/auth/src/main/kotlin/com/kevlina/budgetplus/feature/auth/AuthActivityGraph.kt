package com.kevlina.budgetplus.feature.auth

import androidx.activity.ComponentActivity
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Binds
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Provides

@GraphExtension
interface AuthActivityGraph {

    @Binds val AuthActivity.bind: ComponentActivity

    fun inject(target: AuthActivity)

    @ContributesTo(AppScope::class)
    @GraphExtension.Factory
    interface Factory {
        fun create(@Provides activity: AuthActivity): AuthActivityGraph
    }
}
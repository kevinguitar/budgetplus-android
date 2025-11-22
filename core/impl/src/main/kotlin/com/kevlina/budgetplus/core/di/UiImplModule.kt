package com.kevlina.budgetplus.core.di

import com.kevlina.budgetplus.core.ui.BubbleRepoImpl
import com.kevlina.budgetplus.core.ui.bubble.BubbleRepo
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo

@Module
@ContributesTo(AppScope::class)
@InstallIn(SingletonComponent::class)
interface UiImplModule {

    @Binds
    fun bindBubbleRepo(impl: BubbleRepoImpl): BubbleRepo
}
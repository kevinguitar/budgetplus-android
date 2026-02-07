package com.kevlina.budgetplus.feature.auth

import androidx.lifecycle.ViewModel
import com.kevlina.budgetplus.core.common.di.ViewModelKey
import com.kevlina.budgetplus.core.common.di.ViewModelScope
import dev.zacsweers.metro.ContributesIntoMap

@ViewModelKey(IosAuthViewModel::class)
@ContributesIntoMap(ViewModelScope::class)
internal class IosAuthViewModel(
    val commonAuth: CommonAuthViewModel,
) : ViewModel() {

    fun signInWithGoogle() {
        TODO()
    }

    fun signInWithApple() {
        TODO()
    }
}
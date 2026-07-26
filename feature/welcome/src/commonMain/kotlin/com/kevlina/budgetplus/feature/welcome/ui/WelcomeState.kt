package com.kevlina.budgetplus.feature.welcome.ui

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Stable
data class WelcomeState(
    val bookName: TextFieldState,
    val isCreatingBook: StateFlow<Boolean>,
    val createBook: () -> Unit,
) {
    companion object {
        val preview = WelcomeState(
            bookName = TextFieldState("My book"),
            isCreatingBook = MutableStateFlow(false),
            createBook = {},
        )
    }
}

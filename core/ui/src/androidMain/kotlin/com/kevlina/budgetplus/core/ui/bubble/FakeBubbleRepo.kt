package com.kevlina.budgetplus.core.ui.bubble

import androidx.annotation.VisibleForTesting
import kotlinx.coroutines.flow.MutableStateFlow

@VisibleForTesting
class FakeBubbleRepo : BubbleRepo {

    override val bubble = MutableStateFlow<BubbleDest?>(null)

    override suspend fun addBubbleToQueue(dest: BubbleDest) {
        error("Not yet implemented")
    }

    override fun popBubble() {
        error("Not yet implemented")
    }
}
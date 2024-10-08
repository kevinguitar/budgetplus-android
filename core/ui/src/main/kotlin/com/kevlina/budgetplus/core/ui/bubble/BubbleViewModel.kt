package com.kevlina.budgetplus.core.ui.bubble

import androidx.lifecycle.ViewModel
import javax.inject.Inject

class BubbleViewModel @Inject constructor(
    private val bubbleRepo: BubbleRepo,
) : ViewModel() {

    val destination = bubbleRepo.bubble

    fun dismissBubble() {
        bubbleRepo.popBubble()
    }
}
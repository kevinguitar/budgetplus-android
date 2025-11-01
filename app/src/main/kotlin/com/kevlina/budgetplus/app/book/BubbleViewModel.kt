package com.kevlina.budgetplus.app.book

import androidx.lifecycle.ViewModel
import com.kevlina.budgetplus.core.ui.bubble.BubbleRepo
import javax.inject.Inject

class BubbleViewModel @Inject constructor(
    private val bubbleRepo: BubbleRepo,
) : ViewModel() {

    val destination = bubbleRepo.bubble

    fun dismissBubble() {
        bubbleRepo.popBubble()
    }
}
package com.kevlina.budgetplus.book.bubble.vm

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class BubbleViewModel @Inject constructor(
    private val bubbleRepo: BubbleRepo
) : ViewModel() {

    val destination = bubbleRepo.bubble

    fun dismissBubble() {
        bubbleRepo.popBubble()
    }
}
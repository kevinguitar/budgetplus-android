package com.kevingt.budgetplus.book.bubble.vm

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class BubbleViewModel @Inject constructor(
    private val bubbleRepo: BubbleRepo
) : ViewModel() {

    val destination = bubbleRepo.destination

    fun clearDest() {
        bubbleRepo.clearDestination()
    }
}
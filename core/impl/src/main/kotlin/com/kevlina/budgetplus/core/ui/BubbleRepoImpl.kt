package com.kevlina.budgetplus.core.ui

import com.kevlina.budgetplus.core.common.AppScope
import com.kevlina.budgetplus.core.data.local.PreferenceHolder
import com.kevlina.budgetplus.core.ui.bubble.BubbleDest
import com.kevlina.budgetplus.core.ui.bubble.BubbleRepo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BubbleRepoImpl @Inject constructor(
    @AppScope private val appScope: CoroutineScope,
    private val preferenceHolder: PreferenceHolder,
) : BubbleRepo {

    private val bubblesQueue = mutableListOf<BubbleDest>()

    private val _bubble = MutableStateFlow<BubbleDest?>(null)
    override val bubble: StateFlow<BubbleDest?> = _bubble.asStateFlow()

    private var bubbleShownJob: Job? = null
    private var popBubbleJob: Job? = null

    override fun addBubbleToQueue(dest: BubbleDest) {
        val bubbleKey = dest.key
        if (preferenceHolder.getBoolean(bubbleKey, default = false)) {
            return
        }

        preferenceHolder.setBoolean(bubbleKey, value = true)
        if (bubblesQueue.isEmpty()) {
            showBubble(dest)
        }
        bubblesQueue.add(dest)
    }

    private fun showBubble(dest: BubbleDest) {
        bubbleShownJob?.cancel()
        bubbleShownJob = appScope.launch {
            // Given a short delay to show bubble after UI is presented
            delay(BUBBLE_SHOWN_DELAY)
            _bubble.value = dest
        }
    }

    override fun popBubble() {
        val currentBubble = bubble.value ?: return
        popBubbleJob?.cancel()
        popBubbleJob = appScope.launch {
            // Given a short delay to hide bubble with animation
            delay(BUBBLE_SHOWN_DELAY)
            bubblesQueue.remove(currentBubble)
            _bubble.value = null

            if (bubblesQueue.isNotEmpty()) {
                showBubble(bubblesQueue.first())
            }
        }
    }

    private companion object {
        const val BUBBLE_SHOWN_DELAY = 200L
    }
}
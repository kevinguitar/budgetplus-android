package com.kevlina.budgetplus.book.bubble.vm

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntSize
import com.kevlina.budgetplus.R
import com.kevlina.budgetplus.utils.AppScope
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
class BubbleRepo @Inject constructor(
    @AppScope private val appScope: CoroutineScope
) {

    private val bubblesQueue = arrayListOf<BubbleDest>()
    private val bubbleShownDelay get() = 200L

    private val _bubble = MutableStateFlow<BubbleDest?>(null)
    val bubble: StateFlow<BubbleDest?> = _bubble.asStateFlow()

    private var bubbleShownJob: Job? = null

    fun addBubbleToQueue(dest: BubbleDest) {
        bubblesQueue.add(dest)
        showBubble(dest)
    }

    private fun showBubble(dest: BubbleDest) {
        bubbleShownJob?.cancel()
        bubbleShownJob = appScope.launch {
            // Given a short delay to show bubble after UI is presented
            delay(bubbleShownDelay)
            _bubble.value = dest
        }
    }

    fun popBubble() {
        val currentBubble = bubble.value ?: return
        appScope.launch {
            // Given a short delay to hide bubble with animation
            delay(bubbleShownDelay)
            bubblesQueue.remove(currentBubble)
            _bubble.value = null

            if (bubblesQueue.isNotEmpty()) {
                showBubble(bubblesQueue.last())
            }
        }
    }
}

sealed class BubbleDest {

    abstract val size: IntSize
    abstract val offset: Offset
    abstract val shape: BubbleShape

    abstract val textRes: Int
    abstract val textDirection: BubbleTextDirection

    data class Invite(
        override val size: IntSize,
        override val offset: Offset,
        override val shape: BubbleShape = BubbleShape.Circle,
        override val textRes: Int = R.string.bubble_invite,
        override val textDirection: BubbleTextDirection = BubbleTextDirection.BottomEnd
    ) : BubbleDest()

    data class EditCategoriesHint(
        override val size: IntSize,
        override val offset: Offset,
        override val shape: BubbleShape,
        override val textRes: Int = R.string.bubble_edit_category,
        override val textDirection: BubbleTextDirection = BubbleTextDirection.BottomCenter
    ) : BubbleDest()

    data class SaveCategories(
        override val size: IntSize,
        override val offset: Offset,
        override val shape: BubbleShape = BubbleShape.Circle,
        override val textRes: Int = R.string.bubble_save_category,
        override val textDirection: BubbleTextDirection = BubbleTextDirection.BottomEnd
    ) : BubbleDest()

    data class RecordsSorting(
        override val size: IntSize,
        override val offset: Offset,
        override val shape: BubbleShape = BubbleShape.Circle,
        override val textRes: Int = R.string.bubble_records_sorting,
        override val textDirection: BubbleTextDirection = BubbleTextDirection.BottomEnd
    ) : BubbleDest()

}

enum class BubbleTextDirection {
    TopStart, TopEnd, TopCenter,
    BottomStart, BottomEnd, BottomCenter
}

sealed class BubbleShape {
    object Circle : BubbleShape()
    data class RoundedRect(val corner: Float) : BubbleShape()
}
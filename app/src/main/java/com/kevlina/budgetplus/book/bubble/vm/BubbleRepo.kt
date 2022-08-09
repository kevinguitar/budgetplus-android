package com.kevlina.budgetplus.book.bubble.vm

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntSize
import com.kevlina.budgetplus.R
import com.kevlina.budgetplus.utils.AppScope
import kotlinx.coroutines.CoroutineScope
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

    private val _destination = MutableStateFlow<BubbleDest?>(null)
    val destination: StateFlow<BubbleDest?> = _destination.asStateFlow()

    fun setDestination(dest: BubbleDest) {
        appScope.launch {
            // Given a short delay to show bubble after UI is presented
            delay(1000)
            _destination.value = dest
        }
    }

    fun clearDestination() {
        appScope.launch {
            // Given a short delay to hide bubble with animation
            delay(1000)
            _destination.value = null
        }
    }
}

sealed class BubbleDest {

    abstract val size: IntSize
    abstract val offset: Offset

    abstract val textRes: Int
    abstract val textDirection: BubbleTextDirection

    data class Invite(
        override val size: IntSize,
        override val offset: Offset,
        override val textRes: Int = R.string.bubble_invite,
        override val textDirection: BubbleTextDirection = BubbleTextDirection.BottomEnd
    ) : BubbleDest()

    data class SaveCategories(
        override val size: IntSize,
        override val offset: Offset,
        override val textRes: Int = R.string.bubble_save_category,
        override val textDirection: BubbleTextDirection = BubbleTextDirection.BottomEnd
    ) : BubbleDest()

    data class RecordsSorting(
        override val size: IntSize,
        override val offset: Offset,
        override val textRes: Int = R.string.bubble_records_sorting,
        override val textDirection: BubbleTextDirection = BubbleTextDirection.BottomEnd
    ) : BubbleDest()

}

enum class BubbleTextDirection {
    TopStart, TopEnd, BottomStart, BottomEnd
}